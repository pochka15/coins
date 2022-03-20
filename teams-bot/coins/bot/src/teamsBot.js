const {
  TeamsActivityHandler,
  CardFactory,
  TurnContext,
} = require("botbuilder");
const rawWelcomeCard = require("../adaptiveCards/welcome.json");
const rawHomeCard = require("../adaptiveCards/home.json");
const rawTaskCard = require("../adaptiveCards/task.json");
const ACData = require("adaptivecards-templating");
const CoinsService = require("./api/CoinsService");

/**
 * @typedef {{
 *  name: string,
 *  coinsAmount: number
 * }} Wallet
 */

/**
 *
 * @param {Array<Wallet>} wallets
 * @returns {string} formatted wallets
 */
function formatWallets(wallets) {
  return wallets.map((x) => `- ${x.name} (${x.coinsAmount} coins)`).join("\n");
}

/**
 * @typedef {{
 *  title: string,
 *  status: string
 * }} Task
 */

/**
 *
 * @param {Array<Task>} tasks
 * @returns {string} formatted tasks
 */
function formatTasks(tasks) {
  return tasks.map((x) => `- ${x.title}: ${x.status}`).join("\n");
}

class TeamsBot extends TeamsActivityHandler {
  _coinsService;

  constructor() {
    super();

    this._coinsService = new CoinsService();

    this.onMessage(async (context, next) => {
      TurnContext.removeRecipientMention(context.activity);
      const removedMentionText = TurnContext.removeRecipientMention(
        context.activity
      );

      if (removedMentionText) {
        const text = removedMentionText
          .toLocaleLowerCase()
          .replace(/[\n\r]/g, "")
          .trim();

        // Commands that start with some keyword
        if (text.startsWith("solve")) {
          const taskId = text.slice("solve ".length);
          await this.handleSolveTaskCommand(taskId, context);
        }

        // Commands that match some keyword
        else {
          switch (text) {
            case "help":
            case "welcome": {
              const card = this.renderAdaptiveCard(rawWelcomeCard);
              await context.sendActivity({ attachments: [card] });
              break;
            }
            case "task": {
              const card = this.renderAdaptiveCard(rawTaskCard);
              await context.sendActivity({ attachments: [card] });
              break;
            }
            case "register": {
              const reference = TurnContext.getConversationReference(
                context.activity
              );

              const isOk = await this._coinsService.registerBot(reference);
              const message = isOk
                ? "Bot has been registered"
                : "Bot couldn't be registered, something went wrong";
              await context.sendActivity(`${message}`);
              break;
            }
            case "home": {
              const reference = TurnContext.getConversationReference(
                context.activity
              );
              const id = reference.user.id;
              const data = await this._coinsService.getHomeData(id);
              const card = this.renderAdaptiveCard(rawHomeCard, {
                wallets: formatWallets(data.wallets),
                tasks: formatTasks(data.tasks),
              });
              await context.sendActivity({ attachments: [card] });
              break;
            }
          }
        }
      }

      // By calling next() you ensure that the next BotHandler is run.
      await next();
    });

    // Listen to MembersAdded event, view https://docs.microsoft.com/en-us/microsoftteams/platform/resources/bot-v3/bots-notifications for more events
    this.onMembersAdded(async (context, next) => {
      const membersAdded = context.activity.membersAdded;
      for (let cnt = 0; cnt < membersAdded.length; cnt++) {
        if (membersAdded[cnt].id) {
          const card = this.renderAdaptiveCard(rawWelcomeCard);
          await context.sendActivity({ attachments: [card] });
          break;
        }
      }
      await next();
    });
  }

  // Bind AdaptiveCard with data
  renderAdaptiveCard(rawCardTemplate, dataObj) {
    const cardTemplate = new ACData.Template(rawCardTemplate);
    const cardWithData = cardTemplate.expand({ $root: dataObj });
    return CardFactory.adaptiveCard(cardWithData);
  }

  async onAdaptiveCardInvoke(context, invokeValue) {
    if (invokeValue.action.verb === "create-task") {
      const task = invokeValue.action.data;
      task.title = task.title.trim();
      const conversationReference = TurnContext.getConversationReference(
        context.activity
      );

      // noinspection JSCheckFunctionSignatures
      const isOk = await this._coinsService.createTask({
        ...task,
        roomId: 1,
        userId: conversationReference.user.id,
      });
      if (isOk) {
        await context.sendActivity(
          `Task "${task.title}" was created successfully`
        );
      }
      // noinspection JSValidateTypes
      return { statusCode: 200 };
    }
  }

  async handleSolveTaskCommand(taskId, context) {
    const message = await this._coinsService.solveTask(taskId);
    await context.sendActivity(message);
  }
}

module.exports.TeamsBot = TeamsBot;

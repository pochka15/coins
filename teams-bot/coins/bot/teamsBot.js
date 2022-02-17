const {
  TeamsActivityHandler,
  CardFactory,
  TurnContext,
  teamsNotifyUser,
} = require("botbuilder");
const rawWelcomeCard = require("./adaptiveCards/welcome.json");
const rawLearnCard = require("./adaptiveCards/learn.json");
const ACData = require("adaptivecards-templating");
const { MessageFactory } = require("botbuilder-core");

class TeamsBot extends TeamsActivityHandler {
  constructor() {
    super();

    // record the likeCount
    this.likeCountObj = { likeCount: 0 };

    this.onMessage(async (context, next) => {
      TurnContext.removeRecipientMention(context.activity);
      const text = context.activity.text.trim().toLocaleLowerCase();
   
      // Trigger command by IM text
      switch (text) {
        case "welcome": {
          const card = this.renderAdaptiveCard(rawWelcomeCard);
          await context.sendActivity({ attachments: [card] });
          break;
        }
        case "learn": {
          this.likeCountObj.likeCount = 0;
          const card = this.renderAdaptiveCard(rawLearnCard, this.likeCountObj);
          await context.sendActivity({ attachments: [card] });
          break;
        }
        case "create": {
          await context.sendActivity(
            `Activity created: ${context.activity.id}`
          );
          break;
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

  // Invoked when an action is taken on an Adaptive Card. The Adaptive Card sends an event to the Bot and this
  // method handles that event.
  async onAdaptiveCardInvoke(context, invokeValue) {
    // The verb "userlike" is sent from the Adaptive Card defined in adaptiveCards/learn.json
    if (invokeValue.action.verb === "userlike") {
      this.likeCountObj.likeCount++;
      const card = this.renderAdaptiveCard(rawLearnCard, this.likeCountObj);
      await context.updateActivity({
        type: "message",
        id: context.activity.replyToId,
        attachments: [card],
      });
      return { statusCode: 200 };
    }
  }

  // Bind AdaptiveCard with data
  renderAdaptiveCard(rawCardTemplate, dataObj) {
    const cardTemplate = new ACData.Template(rawCardTemplate);
    const cardWithData = cardTemplate.expand({ $root: dataObj });
    const card = CardFactory.adaptiveCard(cardWithData);
    return card;
  }
}

module.exports.TeamsBot = TeamsBot;

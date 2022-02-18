const {
  TeamsActivityHandler,
  CardFactory,
  TurnContext,
} = require("botbuilder");
const rawWelcomeCard = require("../adaptiveCards/welcome.json");
const ACData = require("adaptivecards-templating");
const CoinsService = require("./api/CoinsService");

class TeamsBot extends TeamsActivityHandler {
  constructor() {
    super();

    this.coinsService = new CoinsService()

    this.onMessage(async (context, next) => {
      TurnContext.removeRecipientMention(context.activity);
      const text = context.activity.text.trim().toLocaleLowerCase();

      switch (text) {
        case "welcome": {
          const card = this.renderAdaptiveCard(rawWelcomeCard);
          await context.sendActivity({attachments: [card]});
          break;
        }
        case "wallet": {
          await context.sendActivity(`You don't have any wallet yet`);
          break;
        }
        case "api": {
          const message = await this.coinsService.getHelloMessage()
          await context.sendActivity(`Got the ${message}`);
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

  // Bind AdaptiveCard with data
  renderAdaptiveCard(rawCardTemplate, dataObj) {
    const cardTemplate = new ACData.Template(rawCardTemplate);
    const cardWithData = cardTemplate.expand({ $root: dataObj });
    return CardFactory.adaptiveCard(cardWithData);
  }
}

module.exports.TeamsBot = TeamsBot;

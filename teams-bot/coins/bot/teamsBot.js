const {
  TeamsActivityHandler,
  CardFactory,
  TurnContext,
  teamsNotifyUser,
} = require("botbuilder");
const rawWelcomeCard = require("./adaptiveCards/welcome.json");
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
        case "wallet": {
          await context.sendActivity(
            `You don't have any wallet yet`
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

  // Bind AdaptiveCard with data
  renderAdaptiveCard(rawCardTemplate, dataObj) {
    const cardTemplate = new ACData.Template(rawCardTemplate);
    const cardWithData = cardTemplate.expand({ $root: dataObj });
    const card = CardFactory.adaptiveCard(cardWithData);
    return card;
  }
}

module.exports.TeamsBot = TeamsBot;

/**
 * Income Dto from the backend. It's used to handle notifications
 */
class Notification {
  /** @type {"TASK_SOLVED"} */
  type;

  /** @type {string} */
  taskTitle;

  /** @type {string} */
  rawConversationReference;
}

/**
 * Controller which is primarily made to handle the requests from the coins api
 */
class CoinsController {
  teamsBot;
  adapter;

  /**
   * ctor
   * @param {TeamsBot} teamsBot
   * @param {BotFrameworkAdapter} adapter
   */
  constructor(teamsBot, adapter) {
    this.teamsBot = teamsBot;
    this.adapter = adapter;
  }

  /**
   *
   * @param req - axios request.
   * @param res - axios response
   */
  async handleNotification(req, res) {
    /** @type {Notification} */
    const notification = req.body;

    const conversationReference = JSON.parse(
      notification.rawConversationReference
    );

    try {
      await this._sendProactiveMessage(
        `Task ${notification.taskTitle} has been solved`,
        conversationReference
      );
    } catch (e) {
      res.send(500, `Couldn't send the proactive message: ${e.message}`);
    }

    res.send(200, "Successfully sent proactive message");
  }

  async _sendProactiveMessage(message, conversationReference) {
    await this.adapter.continueConversation(
      conversationReference,
      async (context) => await context.sendActivity(message)
    );
  }
}

module.exports = { CoinsController };

const axios = require("axios");
const {
  TMP_ENDPOINT,
  NEW_TASK_ENDPOINT,
  BOT_REGISTRATION_ENDPOINT,
  BOT_HOME_ENDPOINT,
} = require("./CoinsApi");

class Task {
  /** @type {string} */
  title;

  /** @type {string} */
  content;

  /** @type {string} */
  deadline;

  /** @type {number} */
  budget;

  /** @type {number} */
  roomId;

  /** @type {string} */
  userId;
}

/**
 * Coins api service. It's primarily made to run procedures on the backend
 */
class CoinsService {
  async getHelloMessage() {
    const response = await axios.get(TMP_ENDPOINT);
    return response.data;
  }

  /**
   * Create a task
   * @param {Task} task
   * @returns {Promise<boolean>} true if server responded with 200
   */
  async createTask(task) {
    const response = await axios.post(NEW_TASK_ENDPOINT, task);
    return response.status === 200;
  }

  // noinspection JSValidateJSDoc
  /**
   * Register bot in the coins service
   * @param {ConversationReference} conversationReference
   * @returns {Promise<boolean>} true if server responded with 200
   */
  async registerBot(conversationReference) {
    const response = await axios.post(
      BOT_REGISTRATION_ENDPOINT,
      conversationReference
    );
    return response.status === 200;
  }

  async getHomeData(userId) {
    const response = await axios.post(BOT_HOME_ENDPOINT, { userId });
    return response.data;
  }
}

module.exports = CoinsService;

const axios = require("axios");
const {TMP_ENDPOINT} = require("./CoinsApi");

class CoinsService {
  async getHelloMessage() {
    // TODO remove
    if (TMP_ENDPOINT === undefined) return "Error: endpoint is undefined";
    const response = await axios.get(TMP_ENDPOINT);
    return response.data;
  }
}

module.exports = CoinsService
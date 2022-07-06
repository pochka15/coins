import axios from 'axios'

const API_URL = process.env.REACT_APP_API_URL
const USOS_LOGIN_ENDPOINT = `${API_URL}/oauth/usos`
const USOS_SERVER_CALLBACK_ENDPOINT = `${API_URL}/oauth/usos-callback`
const TOKENS_KEY = `TOKENS`
export const USOS_CALLBACK_ENDPOINT = 'oauth/usos-callback'

class Auth {
  constructor() {
    this.client = this._createClient()
  }

  startLogin() {
    window.location.href = USOS_LOGIN_ENDPOINT
  }

  async finishLogin() {
    const oauthToken = getQueryVariable('oauth_token')
    const oauthVerifier = getQueryVariable('oauth_verifier')
    const response = await fetch(USOS_SERVER_CALLBACK_ENDPOINT, {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        oauth_token: oauthToken,
        oauth_verifier: oauthVerifier
      })
    })
    if (response.ok) {
      localStorage.setItem(TOKENS_KEY, oauthToken)
    } else {
      const error = new Error(
        'Unable to finish Oauth login.\nServer response status: ' +
          response.status
      )
      error.status = response.status
    }
  }

  logout() {
    localStorage.removeItem(TOKENS_KEY)
  }

  getClient() {
    return this.client
  }

  isLogged() {
    return localStorage.getItem(TOKENS_KEY) !== null
  }

  // noinspection JSValidateJSDoc
  /** @returns {AxiosInstance} */
  _createClient() {
    const client = axios.create({ baseURL: API_URL })
    client.interceptors.request.use(async config => {
      const token = localStorage.getItem(TOKENS_KEY)
      if (token != null) {
        config.headers['Authorization'] = `Bearer ${token}`
      }
      return config
    })
    return client
  }
}

const auth = new Auth()
export default auth

function getQueryVariable(variable) {
  let query = window.location.search.substring(1)
  let vars = query.split('&')
  for (let i = 0; i < vars.length; i++) {
    const pair = vars[i].split('=')
    if (decodeURIComponent(pair[0]) === variable) {
      return decodeURIComponent(pair[1])
    }
  }
}

interface TNotification {
  type: 'error' | 'info' | 'success' | 'loading'
  payload: any
}

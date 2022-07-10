import { Navigate } from 'react-router-dom'
import auth from './auth'

export const ProtectedRoute = ({ children }) => {
  if (!auth.isLogged()) {
    return <Navigate to="/" />
  }
  return children
}

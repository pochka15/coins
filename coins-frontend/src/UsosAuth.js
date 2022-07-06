import React, { useEffect } from 'react'
import auth from './auth'
import { useNavigate } from 'react-router-dom'

function UsosAuth() {
  const navigate = useNavigate()

  useEffect(() => {
    auth.finishLogin().then(() => navigate('/'))
  }, [navigate])

  return <div>Loading</div>
}

export default UsosAuth

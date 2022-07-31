import { ColorModeScript, theme, ChakraProvider } from '@chakra-ui/react'
import React from 'react'
import App from './App'
import reportWebVitals from './reportWebVitals'
import * as serviceWorker from './serviceWorker'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { USOS_CALLBACK_ENDPOINT } from './security/auth'
import UsosAuthCallbackPage from './ui-components/UsosAuthCallbackPage'
import { QueryClient, QueryClientProvider } from 'react-query'
import Admin from './ui-components/admin/Admin'
import AdminLayout from './ui-components/admin/AdminLayout'

const container = document.getElementById('root')
const queryClient = new QueryClient()
const root = createRoot(container)
root.render(
  <>
    <ColorModeScript />
    <ChakraProvider theme={theme}>
      <QueryClientProvider client={queryClient}>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<App />} />
            <Route
              path="/admin"
              element={
                <AdminLayout>
                  <Admin />
                </AdminLayout>
              }
            />
            <Route
              path={USOS_CALLBACK_ENDPOINT}
              element={<UsosAuthCallbackPage />}
            />
          </Routes>
        </BrowserRouter>
      </QueryClientProvider>
    </ChakraProvider>
  </>
)

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://cra.link/PWA
serviceWorker.unregister()

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals()

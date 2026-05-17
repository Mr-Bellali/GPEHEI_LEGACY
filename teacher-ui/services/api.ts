import axios from 'axios'
import { clearToken, getToken } from '@/utils/authUtils'

const API_URL = 'http://localhost:9000'

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers = config.headers ?? {};
    (config.headers as any).Authorization = `Bearer ${token}`;
  }
  return config;
});

let handlingUnauthorized = false

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status
    if (status === 401) {
      clearToken()
      if (typeof window !== 'undefined' && !handlingUnauthorized) {
        handlingUnauthorized = true
        window.location.replace('/')
      }
    }
    return Promise.reject(error)
  }
)

export default api

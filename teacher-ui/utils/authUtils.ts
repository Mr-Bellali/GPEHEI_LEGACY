import { jwtDecode } from 'jwt-decode';

const TOKEN_KEY = 'accessToken'

// Checks if the user is logged in by verifying the presence of an access token
export const isLoggedIn = () => {
  if (typeof window === 'undefined') return false
  return !!localStorage.getItem(TOKEN_KEY);
};

// Retrieves the access token from localStorage
export const getToken = () => {
  if (typeof window === 'undefined') return null
  return localStorage.getItem(TOKEN_KEY);
};

// Clears the access token from localStorage
export const clearToken = () => {
  if (typeof window === 'undefined') return
  localStorage.removeItem(TOKEN_KEY);
};

// Stores the access token in localStorage
export const storeToken = (token: string) => {
  if (typeof window === 'undefined') return
  localStorage.setItem(TOKEN_KEY, token);
};

// Function to check if the token is expired
export const isTokenExpired = (token: any) => {
  if (!token) return true;
  try {
    const decodedToken = jwtDecode(token);
    const currentTime = Date.now() / 1000;
    return decodedToken.exp as number < currentTime;
  } catch (error) {
    console.error('Error decoding token:', error);
    return true;
  }
};

import { jwtDecode } from 'jwt-decode';

// Checks if the user is logged in by verifying the presence of an access token
export const isLoggedIn = () => {
  return !!localStorage.getItem("accessToken");
};

// Retrieves the access token from localStorage
export const getToken = () => {
  return localStorage.getItem("accessToken");
};

// Clears the access token from localStorage
export const clearToken = () => {
  localStorage.removeItem("accessToken");
};

// Stores the access token in localStorage
export const storeToken = (token: string) => {
  localStorage.setItem("accessToken", token);
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
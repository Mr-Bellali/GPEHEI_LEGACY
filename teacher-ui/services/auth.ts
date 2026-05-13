import api from './api';

// Function to login
export async function getToken(email:string, password: string): Promise<any> {
  const res = await api.post('/auth/login', {
    email,
    password
  } );
  return res.data
}

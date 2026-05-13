'use client'

import React, { useState } from 'react'
import LoginForm from '@/components/forms/LoginForm';
import { getToken } from '@/services/auth';
import { storeToken } from '@/utils/authUtils';
import { useRouter } from 'next/navigation';

const AuthTemplate = () => {
    const router = useRouter();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleLoginSubmit = async (data: { email: string; password: string; remember: boolean }) => {
        setLoading(true);
        setError(null);
        
        try {
            const response = await getToken(data.email, data.password);
            
            // Check if response status is 200 (or if token exists in response)
            if (response && response.token) {
                // Store the token in localStorage
                storeToken(response.token);
                
                // Redirect to dashboard or home page
                router.push('/home');
            } else {
                setError('Login failed. Please check your credentials.');
            }
        } catch (err: any) {
            const errorMessage = err.response?.data?.message || 'An error occurred during login';
            setError(errorMessage);
            console.error('Login error:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="w-full h-screen p-6 bg-[#3D348B] flex">
            <div className="w-1/2 flex flex-col items-start justify-between h-full">
                <h1 className="font-medium text-white text-3xl">
                    EHEI
                </h1>
                <div className="w-full h-full flex justify-center items-center">
                    <p className="text-[500px] text-[#7678ED]/50">
                        L
                    </p>
                </div>
                <p className="text-[#D1D1D1] text-xs font-medium">
                    @G321 2026 All rights reserved
                </p>
            </div>
            <div className="w-1/2 h-full bg-[#7678ED] rounded-[10px] px-6 pb-6 pt-[15%] flex flex-col">
               <LoginForm onSubmit={handleLoginSubmit} loading={loading} />
               {error && (
                   <div className="mt-4 p-3 bg-red-500/20 border border-red-500 rounded text-red-100 text-sm">
                       {error}
                   </div>
               )}
               {loading && (
                   <div className="mt-4 text-center text-white/70">
                       Logging in...
                   </div>
               )}
            </div>
        </div>
    )
}

export default AuthTemplate
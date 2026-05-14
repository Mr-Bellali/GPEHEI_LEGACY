'use client'

import React from 'react';

export default function ProfileCard() {
  return (
    <div className="bg-white rounded-[24px] overflow-hidden border border-gray-100">
      <div className="h-24 bg-cover bg-center" style={{ backgroundImage: 'url("https://images.unsplash.com/photo-1541339907198-e08756ebafe3?q=80&w=1000&auto=format&fit=crop")' }} />
      <div className="px-6 pb-6 text-center">
        <div className="relative -mt-10 mb-3 flex justify-center">
          <div className="w-20 h-20 rounded-3xl border-4 border-white bg-[#7678ED] flex items-center justify-center text-white text-2xl font-bold">
            J
          </div>
        </div>
        <h2 className="text-xl font-bold text-[#3D348B]">Jhon Doe</h2>
        <p className="text-xs text-gray-400 mb-2">@jhon.doe</p>
        <p className="text-[11px] text-[#7678ED] font-medium leading-relaxed px-2">
          ✨ 3rd year Student Genie Informatique @EHEI ✨
        </p>
        
        <div className="grid grid-cols-2 gap-0 mt-6 pt-6 border-t border-gray-100">
          <div className="border-r border-gray-100">
            <p className="text-lg font-bold text-[#3D348B]">60</p>
            <p className="text-[10px] text-gray-400 uppercase tracking-wider">Following</p>
          </div>
          <div>
            <p className="text-lg font-bold text-[#3D348B]">5</p>
            <p className="text-[10px] text-gray-400 uppercase tracking-wider">Followers</p>
          </div>
        </div>
        
        <button className="mt-6 w-full text-sm font-bold text-[#3D348B] hover:text-[#7678ED] transition-colors">
          My Profile
        </button>
      </div>
    </div>
  );
}

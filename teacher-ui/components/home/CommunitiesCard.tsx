'use client'

import React from 'react';

const communities = [
  { name: 'Sportif club', desc: 'Join us today and desco...', image: 'https://images.unsplash.com/photo-1517649763962-0c623066013b?q=80&w=100&auto=format&fit=crop' },
  { name: 'Hiking', desc: 'We love advantures', image: 'https://images.unsplash.com/photo-1551632811-561732d1e306?q=80&w=100&auto=format&fit=crop' },
  { name: 'Art & study', desc: 'Be creative and smart', image: 'https://images.unsplash.com/photo-1460661419201-fd4ce186860d?q=80&w=100&auto=format&fit=crop' },
];

export default function CommunitiesCard() {
  return (
    <div className="bg-white rounded-[24px] p-6 border border-gray-100">
      <div className="flex justify-between items-center mb-5">
        <h3 className="font-bold text-[#404359] text-sm">Comunities</h3>
      </div>
      <div className="flex flex-col gap-4">
        {communities.map((c, i) => (
          <div key={i} className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl overflow-hidden shrink-0">
              <img src={c.image} alt={c.name} className="w-full h-full object-cover" />
            </div>
            <div className="flex-1 overflow-hidden">
              <p className="text-xs font-bold text-[#3D348B] truncate">{c.name}</p>
              <p className="text-[10px] text-gray-400 truncate">{c.desc}</p>
            </div>
            <button className="px-4 py-1.5 bg-[#3D348B] text-white text-[10px] font-bold rounded-lg hover:bg-[#7678ED] transition-colors">
              Join
            </button>
          </div>
        ))}
      </div>
      <button className="text-[10px] text-[#7678ED] font-medium hover:underline self-end mt-4">See more</button>
    </div>
  );
}

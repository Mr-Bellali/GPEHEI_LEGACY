'use client'

import React from 'react';

const people = [
  { name: "Person's name", username: '@username', image: 'https://api.dicebear.com/7.x/avataaars/svg?seed=1' },
  { name: "Person's name", username: '@username', image: 'https://api.dicebear.com/7.x/avataaars/svg?seed=2' },
  { name: "Person's name", username: '@username', image: 'https://api.dicebear.com/7.x/avataaars/svg?seed=3' },
];

export default function PeopleCard() {
  return (
    <div className="bg-white rounded-[24px] p-6 border border-gray-100">
      <div className="flex justify-between items-center mb-5">
        <h3 className="font-bold text-[#404359] text-sm">People</h3>
      </div>
      <div className="flex flex-col gap-4">
        {people.map((p, i) => (
          <div key={i} className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl overflow-hidden shrink-0 bg-[#7678ED] flex items-center justify-center text-white text-xs font-bold">
              {p.name[0]}
            </div>
            <div className="flex-1 overflow-hidden">
              <p className="text-xs font-bold text-[#3D348B] truncate">{p.name}</p>
              <p className="text-[10px] text-gray-400 truncate">{p.username}</p>
            </div>
            <button className="px-4 py-1.5 bg-[#3D348B] text-white text-[10px] font-bold rounded-lg hover:bg-[#7678ED] transition-colors">
              Follow
            </button>
          </div>
        ))}
      </div>
      <button className="text-[10px] text-[#7678ED] font-medium hover:underline self-end mt-4">See more</button>
    </div>
  );
}

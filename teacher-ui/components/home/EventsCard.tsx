'use client'

import React from 'react';

const events = [
  { name: 'Even name', date: 'Event date here', image: 'https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?q=80&w=100&auto=format&fit=crop' },
  { name: 'Even name', date: 'Event date here', image: 'https://images.unsplash.com/photo-1540575861501-7ad0582373f1?q=80&w=100&auto=format&fit=crop' },
];

export default function EventsCard() {
  return (
    <div className="bg-white rounded-[24px] p-6 border border-gray-100">
      <div className="flex justify-between items-center mb-5">
        <h3 className="font-bold text-[#404359] text-sm">Events</h3>
      </div>
      <div className="flex flex-col gap-4">
        {events.map((e, i) => (
          <div key={i} className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl overflow-hidden shrink-0">
              <img src={e.image} alt={e.name} className="w-full h-full object-cover" />
            </div>
            <div>
              <p className="text-xs font-bold text-[#3D348B]">{e.name}</p>
              <p className="text-[10px] text-gray-400">{e.date}</p>
            </div>
          </div>
        ))}
      </div>
      <button className="text-[10px] text-[#7678ED] font-medium hover:underline self-end mt-4">See more</button>
    </div>
  );
}

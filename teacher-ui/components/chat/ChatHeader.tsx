'use client'

import React from 'react';
import { MoreVertical, Phone, Video, Info } from 'lucide-react';

export default function ChatHeader(props: { title: string }) {
  const initial = props.title?.[0]?.toUpperCase?.() ?? '?';
  return (
    <div className="p-6 border-b border-gray-100 flex justify-between items-center">
      <div className="flex items-center gap-4">
        <div className="w-12 h-12 rounded-2xl bg-[#7678ED] flex items-center justify-center text-white font-bold">
          {initial}
        </div>
        <div>
          <h3 className="font-bold text-[#3D348B]">{props.title}</h3>
          <p className="text-xs text-gray-400 font-medium">Online</p>
        </div>
      </div>
      <div className="flex items-center gap-2">
        <button className="p-2 hover:bg-gray-50 rounded-xl text-gray-400 transition-colors">
          <Phone className="w-5 h-5" />
        </button>
        <button className="p-2 hover:bg-gray-50 rounded-xl text-gray-400 transition-colors">
          <Video className="w-5 h-5" />
        </button>
        <button className="p-2 hover:bg-gray-50 rounded-xl text-gray-400 transition-colors">
          <Info className="w-5 h-5" />
        </button>
        <button className="p-2 hover:bg-gray-50 rounded-xl text-gray-400 transition-colors">
          <MoreVertical className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
}


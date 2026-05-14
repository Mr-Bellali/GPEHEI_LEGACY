'use client'

import React, { useState } from 'react';
import { Send } from 'lucide-react';

export default function MessageComposer(props: { disabled: boolean; onSend: (content: string) => void }) {
  const [value, setValue] = useState('');

  const send = () => {
    const trimmed = value.trim();
    if (!trimmed) return;
    props.onSend(trimmed);
    setValue('');
  };

  return (
    <div className="p-6 bg-white">
      <div className="flex items-center gap-4 bg-gray-50 rounded-[24px] p-2 pl-6 border border-gray-100">
        <input
          type="text"
          placeholder="Type your message here..."
          className="flex-1 bg-transparent border-none outline-none text-sm py-2"
          value={value}
          onChange={(e) => setValue(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') {
              e.preventDefault();
              send();
            }
          }}
          disabled={props.disabled}
        />
        <button
          disabled={props.disabled || !value.trim()}
          onClick={send}
          className="w-10 h-10 bg-[#3D348B] text-white rounded-2xl flex items-center justify-center hover:bg-[#2d2570] transition-all disabled:opacity-40"
        >
          <Send className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
}


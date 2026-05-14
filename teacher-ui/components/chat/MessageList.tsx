'use client'

import React, { useEffect, useMemo, useRef } from 'react';
import type { ChatMessage } from '@/services/chat';

export default function MessageList(props: { messages: ChatMessage[]; currentUserId: number | null }) {
  const ref = useRef<HTMLDivElement | null>(null);

  const ordered = useMemo(() => {
    return [...props.messages].sort((a, b) => a.id - b.id);
  }, [props.messages]);

  useEffect(() => {
    ref.current?.scrollTo({ top: ref.current.scrollHeight });
  }, [ordered.length]);

  return (
    <div ref={ref} className="flex-1 overflow-y-auto p-6 flex flex-col gap-6 bg-gray-50/50">
      {ordered.map((m) => {
        const isMe = props.currentUserId != null && m.sender_id === props.currentUserId;
        return (
          <div key={m.id} className={`flex flex-col ${isMe ? 'items-end' : 'items-start'}`}>
            <div
              className={`max-w-[70%] p-4 rounded-2xl text-sm ${
                isMe ? 'bg-[#3D348B] text-white rounded-tr-none' : 'bg-white text-[#404359] rounded-tl-none border border-gray-100'
              }`}
            >
              {m.content}
            </div>
          </div>
        );
      })}
    </div>
  );
}


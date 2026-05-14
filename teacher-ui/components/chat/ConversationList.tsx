'use client'

import React from 'react';
import { Search } from 'lucide-react';
import type { ChatConversation, ChatUser } from '@/services/chat';

export default function ConversationList(props: {
  conversations: ChatConversation[];
  users: ChatUser[];
  activeConversationId: number | null;
  onSelectConversation: (id: number) => void;
  onSelectSuggestion: (user: ChatUser) => void;
  search: string;
  setSearch: (v: string) => void;
}) {
  const filtered = props.conversations.filter((c) =>
    c.display_name.toLowerCase().includes(props.search.toLowerCase())
  );

  const suggestions = props.users
    .filter((u) => u.conversation_id === 0)
    .filter((u) => (`${u.first_name} ${u.last_name}`).toLowerCase().includes(props.search.toLowerCase()));

  return (
    <div className="w-80 flex-shrink-0 bg-white rounded-[32px] border border-gray-100 flex flex-col overflow-hidden">
      <div className="p-6 border-b border-gray-100">
        <h1 className="text-2xl font-bold text-[#3D348B] mb-4">Messages</h1>
        <div className="flex items-center bg-gray-50 rounded-2xl px-4 py-2 gap-3">
          <Search className="w-4 h-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search chats..."
            className="bg-transparent border-none outline-none text-sm w-full"
            value={props.search}
            onChange={(e) => props.setSearch(e.target.value)}
          />
        </div>
      </div>

      <div className="flex-1 overflow-y-auto">
        <div className="px-6 pt-4 pb-2 text-[11px] font-bold text-gray-400 uppercase tracking-wider">
          Conversations
        </div>
        {filtered.map((c) => {
          const initial = c.display_name?.[0]?.toUpperCase?.() ?? '?';
          const isActive = props.activeConversationId === c.id;
          return (
            <div
              key={c.id}
              onClick={() => props.onSelectConversation(c.id)}
              className={`flex items-center gap-4 p-4 cursor-pointer transition-all ${isActive ? 'bg-[#7678ED]/10' : 'hover:bg-gray-50'}`}
            >
              <div className="relative">
                <div className={`w-12 h-12 rounded-2xl flex items-center justify-center text-white font-bold text-sm ${c.type === 'group' ? 'bg-[#3D348B]' : 'bg-[#7678ED]'}`}>
                  {initial}
                </div>
              </div>
              <div className="flex-1 overflow-hidden">
                <div className="flex justify-between items-center mb-0.5">
                  <h3 className="font-bold text-sm text-[#3D348B] truncate">{c.display_name}</h3>
                  <span className="text-[10px] text-gray-400">{c.updated_at || ''}</span>
                </div>
                <p className="text-xs text-gray-500 truncate">{c.last_message || ''}</p>
              </div>
            </div>
          );
        })}

        <div className="px-6 pt-6 pb-2 text-[11px] font-bold text-gray-400 uppercase tracking-wider">
          Suggestions
        </div>
        {suggestions.map((u) => {
          const initial = u.first_name?.[0]?.toUpperCase?.() ?? '?';
          return (
            <div
              key={`${u.role}:${u.id}`}
              onClick={() => props.onSelectSuggestion(u)}
              className="flex items-center gap-4 p-4 cursor-pointer transition-all hover:bg-gray-50 opacity-50"
            >
              <div className="relative">
                <div className="w-12 h-12 rounded-2xl flex items-center justify-center text-white font-bold text-sm bg-[#7678ED]">
                  {initial}
                </div>
              </div>
              <div className="flex-1 overflow-hidden">
                <div className="flex justify-between items-center mb-0.5">
                  <h3 className="font-bold text-sm text-[#3D348B] truncate">
                    {u.first_name} {u.last_name}
                  </h3>
                  <span className="text-[10px] text-gray-400">{u.role}</span>
                </div>
                <p className="text-xs text-gray-500 truncate">{u.email}</p>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

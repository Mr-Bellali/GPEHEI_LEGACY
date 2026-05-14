 'use client'

 import React, { useEffect, useMemo, useRef, useState } from 'react';
 import ConversationList from '@/components/chat/ConversationList';
 import ChatHeader from '@/components/chat/ChatHeader';
 import MessageList from '@/components/chat/MessageList';
 import MessageComposer from '@/components/chat/MessageComposer';
 import type { ChatConversation, ChatEvent, ChatMessage, ChatClient, ChatUser } from '@/services/chat';
 import { createChatClient } from '@/services/chat';

 const Chat = () => {
  const clientRef = useRef<ChatClient | null>(null);
  const activeConversationIdRef = useRef<number | null>(null);
  const [conversations, setConversations] = useState<ChatConversation[]>([]);
  const [users, setUsers] = useState<ChatUser[]>([]);
  const [messagesByConversation, setMessagesByConversation] = useState<Record<number, ChatMessage[]>>({});
  const [activeConversationId, setActiveConversationId] = useState<number | null>(null);
  const [selectedUser, setSelectedUser] = useState<ChatUser | null>(null);
  const [search, setSearch] = useState('');
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);

  const activeConversation = useMemo(() => {
    if (activeConversationId == null) return null;
    return conversations.find((c) => c.id === activeConversationId) ?? null;
  }, [conversations, activeConversationId]);

  const activeMessages = useMemo(() => {
    if (activeConversationId == null) return [];
    return messagesByConversation[activeConversationId] ?? [];
  }, [messagesByConversation, activeConversationId]);

  useEffect(() => {
    const onEvent = (evt: ChatEvent) => {
      if (evt.event === 'auth') {
        setCurrentUserId(evt.data.id);
        return;
      }

      if (evt.event === 'conversations') {
        setConversations(evt.data);
        if (activeConversationIdRef.current == null && evt.data.length > 0) {
          setActiveConversationId(evt.data[0].id);
          activeConversationIdRef.current = evt.data[0].id;
          setSelectedUser(null);
          clientRef.current?.getMessages(evt.data[0].id);
        }
        return;
      }

      if (evt.event === 'users') {
        setUsers(evt.data);
        return;
      }

      if (evt.event === 'messages') {
        setMessagesByConversation((prev) => ({ ...prev, [evt.data.conversation_id]: evt.data.messages }));
        return;
      }

      if (evt.event === 'message') {
        const msg = evt.data;
        setMessagesByConversation((prev) => {
          const existing = prev[msg.conversation_id] ?? [];
          return { ...prev, [msg.conversation_id]: [...existing, msg] };
        });
        setConversations((prev) =>
          prev.map((c) => (c.id === msg.conversation_id ? { ...c, last_message: msg.content } : c))
        );

        if (selectedUser && activeConversationIdRef.current == null) {
          setActiveConversationId(msg.conversation_id);
          activeConversationIdRef.current = msg.conversation_id;
          setSelectedUser(null);
          clientRef.current?.getMessages(msg.conversation_id);
        }
      }
    };

    clientRef.current = createChatClient({ onEvent });
    clientRef.current.connect();
    return () => {
      clientRef.current?.disconnect();
      clientRef.current = null;
    };
  }, []);

  return (
    <div className="flex h-full mx-auto px-4 pb-10 gap-6">
      <ConversationList
        conversations={conversations}
        users={users}
        activeConversationId={activeConversationId}
        onSelectConversation={(id) => {
          setActiveConversationId(id);
          activeConversationIdRef.current = id;
          setSelectedUser(null);
          clientRef.current?.getMessages(id);
        }}
        onSelectSuggestion={(u) => {
          setSelectedUser(u);
          setActiveConversationId(null);
          activeConversationIdRef.current = null;
        }}
        search={search}
        setSearch={setSearch}
      />

      {/* Main Chat Area */}
      <div className="flex-1 bg-white rounded-[32px] border border-gray-100 flex flex-col overflow-hidden">
        {activeConversationId == null && selectedUser == null ? (
          <div className="flex-1 flex items-center justify-center text-sm text-gray-400">
            Select a conversation to start chatting
          </div>
        ) : (
          <>
            <ChatHeader
              title={
                activeConversation?.display_name ??
                (selectedUser ? `${selectedUser.first_name} ${selectedUser.last_name}` : 'Chat')
              }
            />
            <MessageList messages={activeMessages} currentUserId={currentUserId} />
            <MessageComposer
              disabled={false}
              onSend={(content) => {
                if (activeConversationId != null) {
                  clientRef.current?.sendText(activeConversationId, content);
                  return;
                }
                if (selectedUser) {
                  clientRef.current?.sendToUser(selectedUser.role, selectedUser.id, content);
                }
              }}
            />
          </>
        )}
      </div>
    </div>
  );
};

export default Chat;

'use client'

import { getToken } from '@/utils/authUtils';

export type ChatEvent =
  | { event: 'auth'; data: { id: number; role: string; first_name: string; last_name: string; email: string } }
  | { event: 'conversations'; data: ChatConversation[] }
  | { event: 'users'; data: ChatUser[] }
  | { event: 'messages'; data: { conversation_id: number; messages: ChatMessage[] } }
  | { event: 'message'; data: ChatMessage };

export type ChatConversation = {
  id: number;
  display_name: string;
  type: 'pair' | 'group';
  last_message: string;
  last_message_type: string;
  updated_at: string;
};

export type ChatMessage = {
  id: number;
  content: string;
  type: 'text' | 'media';
  mime_type: string;
  file_name: string;
  sender_id: number;
  sender_name: string;
  sender_role: string;
  conversation_id: number;
  created_at: string;
};

export type ChatUser = {
  id: number;
  role: 'teacher' | 'student';
  first_name: string;
  last_name: string;
  email: string;
  conversation_id: number;
};

export type ChatClient = {
  connect: () => void;
  disconnect: () => void;
  getConversations: () => void;
  getUsers: () => void;
  getMessages: (conversationId: number, limit?: number, beforeId?: number) => void;
  sendText: (conversationId: number, content: string) => void;
  sendToUser: (toRole: 'teacher' | 'student', toUserId: number, content: string) => void;
};

export function createChatClient(params?: {
  url?: string;
  onEvent?: (evt: ChatEvent) => void;
  onOpen?: () => void;
  onClose?: () => void;
  onError?: (e: Event) => void;
}): ChatClient {
  const url = params?.url ?? 'ws://localhost:8080';
  let ws: WebSocket | null = null;
  let isConnected = false;

  const sendJson = (payload: unknown) => {
    if (!ws || ws.readyState !== WebSocket.OPEN) return;
    ws.send(JSON.stringify(payload));
  };

  const connect = () => {
    if (isConnected) return;
    ws = new WebSocket(url);

    ws.onopen = () => {
      isConnected = true;
      params?.onOpen?.();
      const token = getToken();
      if (token) ws?.send(token);
      sendJson({ event: 'getConversations' });
      sendJson({ event: 'getUsers' });
    };

    ws.onmessage = (msg) => {
      try {
        const parsed = JSON.parse(String(msg.data)) as ChatEvent;
        params?.onEvent?.(parsed);
      } catch {
        // ignore
      }
    };

    ws.onerror = (e) => {
      params?.onError?.(e);
    };

    ws.onclose = () => {
      isConnected = false;
      ws = null;
      params?.onClose?.();
    };
  };

  const disconnect = () => {
    if (!ws) return;
    ws.close();
  };

  const getConversations = () => {
    sendJson({ event: 'getConversations' });
  };

  const getUsers = () => {
    sendJson({ event: 'getUsers' });
  };

  const getMessages = (conversationId: number, limit = 50, beforeId = 0) => {
    sendJson({ event: 'getMessages', conversation_id: conversationId, limit, before_id: beforeId });
  };

  const sendText = (conversationId: number, content: string) => {
    sendJson({ event: 'text', conversation_id: conversationId, content });
  };

  const sendToUser = (toRole: 'teacher' | 'student', toUserId: number, content: string) => {
    sendJson({ event: 'sendToUser', to_role: toRole, to_user_id: toUserId, content });
  };

  return { connect, disconnect, getConversations, getUsers, getMessages, sendText, sendToUser };
}

import React, { useState } from 'react';
import { Search, Send, MoreVertical, Phone, Video, Info, User, Users } from 'lucide-react';

const Chat = () => {
  const [activeChat, setActiveChat] = useState(1);
  const [message, setMessage] = useState('');

  const contacts = [
    { id: 1, name: 'Mohammed Ali', type: 'student', lastMessage: 'Professor, I have a question about HW1', time: '10:30 AM', avatar: 'MA', online: true },
    { id: 2, name: 'Fatima Hassan', type: 'student', lastMessage: 'Thank you for the explanation!', time: 'Yesterday', avatar: 'FH', online: false },
    { id: 3, name: 'Faculty Group', type: 'group', lastMessage: 'Meeting at 2 PM today', time: 'Yesterday', avatar: 'FG', online: true },
    { id: 4, name: 'Achraf Elabouye', type: 'admin', lastMessage: 'System maintenance tonight', time: '2 days ago', avatar: 'AE', online: false },
  ];

  const messages = [
    { id: 1, sender: 'Mohammed Ali', content: 'Professor, I have a question about HW1', time: '10:30 AM', isMe: false },
    { id: 2, sender: 'Me', content: 'Sure Mohammed, what is it?', time: '10:35 AM', isMe: true },
    { id: 3, sender: 'Mohammed Ali', content: 'Is the deadline flexible? I had some technical issues.', time: '10:36 AM', isMe: false },
    { id: 4, sender: 'Me', content: 'I can give you an extra 24 hours. Please submit it by tomorrow evening.', time: '10:40 AM', isMe: true },
  ];

  return (
    <div className="flex h-[calc(100vh-100px)] max-w-7xl mx-auto px-4 pb-4 gap-6">
      {/* Sidebar - Contacts */}
      <div className="w-80 flex-shrink-0 bg-white rounded-[32px] shadow-sm flex flex-col overflow-hidden">
        <div className="p-6 border-b border-gray-100">
          <h1 className="text-2xl font-bold text-[#3D348B] mb-4">Messages</h1>
          <div className="flex items-center bg-gray-50 rounded-2xl px-4 py-2 gap-3">
            <Search className="w-4 h-4 text-gray-400" />
            <input 
              type="text" 
              placeholder="Search chats..." 
              className="bg-transparent border-none outline-none text-sm w-full"
            />
          </div>
        </div>
        <div className="flex-1 overflow-y-auto">
          {contacts.map((contact) => (
            <div 
              key={contact.id}
              onClick={() => setActiveChat(contact.id)}
              className={`flex items-center gap-4 p-4 cursor-pointer transition-all ${activeChat === contact.id ? 'bg-[#7678ED]/10' : 'hover:bg-gray-50'}`}
            >
              <div className="relative">
                <div className={`w-12 h-12 rounded-2xl flex items-center justify-center text-white font-bold text-sm ${contact.type === 'group' ? 'bg-[#3D348B]' : 'bg-[#7678ED]'}`}>
                  {contact.avatar}
                </div>
                {contact.online && (
                  <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-green-500 border-2 border-white rounded-full" />
                )}
              </div>
              <div className="flex-1 overflow-hidden">
                <div className="flex justify-between items-center mb-0.5">
                  <h3 className="font-bold text-sm text-[#3D348B] truncate">{contact.name}</h3>
                  <span className="text-[10px] text-gray-400">{contact.time}</span>
                </div>
                <p className="text-xs text-gray-500 truncate">{contact.lastMessage}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="flex-1 bg-white rounded-[32px] shadow-sm flex flex-col overflow-hidden">
        {/* Chat Header */}
        <div className="p-6 border-b border-gray-100 flex justify-between items-center">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-2xl bg-[#7678ED] flex items-center justify-center text-white font-bold">
              {contacts.find(c => c.id === activeChat)?.avatar}
            </div>
            <div>
              <h3 className="font-bold text-[#3D348B]">{contacts.find(c => c.id === activeChat)?.name}</h3>
              <p className="text-xs text-green-500 font-medium">Online</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <button className="p-2 hover:bg-gray-50 rounded-xl text-gray-400 transition-colors"><Phone className="w-5 h-5" /></button>
            <button className="p-2 hover:bg-gray-50 rounded-xl text-gray-400 transition-colors"><Video className="w-5 h-5" /></button>
            <button className="p-2 hover:bg-gray-50 rounded-xl text-gray-400 transition-colors"><Info className="w-5 h-5" /></button>
            <button className="p-2 hover:bg-gray-50 rounded-xl text-gray-400 transition-colors"><MoreVertical className="w-5 h-5" /></button>
          </div>
        </div>

        {/* Messages Feed */}
        <div className="flex-1 overflow-y-auto p-6 flex flex-col gap-6 bg-gray-50/50">
          {messages.map((msg) => (
            <div key={msg.id} className={`flex flex-col ${msg.isMe ? 'items-end' : 'items-start'}`}>
              <div className={`max-w-[70%] p-4 rounded-2xl text-sm ${msg.isMe ? 'bg-[#3D348B] text-white rounded-tr-none' : 'bg-white text-[#404359] rounded-tl-none shadow-sm'}`}>
                {msg.content}
              </div>
              <span className="text-[10px] text-gray-400 mt-1">{msg.time}</span>
            </div>
          ))}
        </div>

        {/* Message Input */}
        <div className="p-6 bg-white">
          <div className="flex items-center gap-4 bg-gray-50 rounded-[24px] p-2 pl-6 shadow-inner">
            <input 
              type="text" 
              placeholder="Type your message here..." 
              className="flex-1 bg-transparent border-none outline-none text-sm py-2"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
            />
            <button 
              className="w-10 h-10 bg-[#3D348B] text-white rounded-2xl flex items-center justify-center hover:bg-[#2d2570] transition-all"
            >
              <Send className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Chat;
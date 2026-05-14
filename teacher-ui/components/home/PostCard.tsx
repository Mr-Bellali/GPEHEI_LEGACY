'use client'

import React from 'react';
import { MoreHorizontal, ArrowBigUp, MessageSquare, Share2 } from 'lucide-react';

interface PostCardProps {
  post: any;
  getRelativeTime: (date: string) => string;
}

export default function PostCard({ post, getRelativeTime }: PostCardProps) {
  const firstName = post.teacher_id ? post.teacher_first : post.student_first;
  const lastName = post.teacher_id ? post.teacher_last : post.student_last;
  const initial = firstName ? firstName[0] : (lastName ? lastName[0] : '?');

  return (
    <div className="bg-white rounded-[24px] p-6 border border-gray-100 flex flex-col gap-4">
      <div className="flex justify-between items-start">
        <div className="flex gap-4">
          <div className="w-12 h-12 rounded-2xl bg-[#7678ED] flex items-center justify-center text-white font-bold text-lg">
            {initial}
          </div>
          <div>
            <h4 className="font-bold text-[#3D348B] text-sm">
              {firstName} {lastName}
            </h4>
            <p className="text-[10px] text-gray-400">{getRelativeTime(post.created_at || new Date().toISOString())}</p>
          </div>
        </div>
        <button className="text-gray-300 hover:text-gray-600">
          <MoreHorizontal className="w-5 h-5" />
        </button>
      </div>

      <div className="flex flex-col gap-4">
        <p className="text-sm text-[#404359] leading-relaxed">
          {post.content}
        </p>
        
        {post.image_link && (
          <div className="rounded-2xl overflow-hidden bg-gray-50 border border-gray-100">
            <img 
              src={`http://localhost:9000${post.image_link}`} 
              alt="post content" 
              className="w-full h-auto max-h-[500px] object-cover"
            />
          </div>
        )}
      </div>

      <div className="flex items-center gap-6 pt-2 border-t border-gray-50">
        <div className="flex items-center gap-1.5 text-gray-400 hover:text-[#3D348B] transition-colors cursor-pointer group">
          <ArrowBigUp className="w-5 h-5 group-hover:fill-[#3D348B]/10" />
          <span className="text-xs font-bold">{post.vote_count || 20}</span>
        </div>
        <div className="flex items-center gap-1.5 text-gray-400 hover:text-[#3D348B] transition-colors cursor-pointer">
          <MessageSquare className="w-5 h-5" />
          <span className="text-xs font-bold">{post.comment_count || 5}</span>
        </div>
        <div className="flex-1">
          <input 
            type="text" 
            placeholder="Write your comment"
            className="w-full bg-gray-50 rounded-xl px-4 py-2 text-xs outline-none border-none focus:ring-0"
          />
        </div>
        <button className="text-gray-300 hover:text-[#7678ED] transition-colors">
          <Share2 className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
}

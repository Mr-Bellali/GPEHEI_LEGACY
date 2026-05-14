'use client'

import React, { useState, useEffect } from 'react';
import { MoreHorizontal, ArrowBigUp, MessageSquare, Share2, Send, Loader2 } from 'lucide-react';
import { getComments, createComment } from '@/services/workspace';

interface PostCardProps {
  post: any;
  getRelativeTime: (date: string) => string;
}

export default function PostCard({ post, getRelativeTime }: PostCardProps) {
  const [showComments, setShowComments] = useState(false);
  const [comments, setComments] = useState<any[]>([]);
  const [loadingComments, setLoadingComments] = useState(false);
  const [newComment, setNewComment] = useState('');
  const [submittingComment, setSubmittingComment] = useState(false);

  const firstName = post.teacher_id ? post.teacher_first : post.student_first;
  const lastName = post.teacher_id ? post.teacher_last : post.student_last;
  const initial = firstName ? firstName[0] : (lastName ? lastName[0] : '?');

  useEffect(() => {
    if (showComments) {
      fetchComments();
    }
  }, [showComments]);

  const fetchComments = async () => {
    setLoadingComments(true);
    try {
      const data = await getComments(post.id);
      setComments(data);
    } catch (error) {
      console.error('Failed to fetch comments:', error);
    } finally {
      setLoadingComments(false);
    }
  };

  const handleCommentSubmit = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!newComment.trim()) return;

    setSubmittingComment(true);
    try {
      await createComment({
        content: newComment,
        post_id: post.id
      });
      setNewComment('');
      fetchComments();
    } catch (error) {
      console.error('Failed to add comment:', error);
    } finally {
      setSubmittingComment(false);
    }
  };

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
        
        {post.image_base64 && (
          <div className="rounded-2xl overflow-hidden bg-gray-50 border border-gray-100">
            <img 
              src={post.image_base64} 
              alt="post content" 
              className="w-full h-auto max-h-[500px] object-cover"
            />
          </div>
        )}
      </div>

      <div className="flex items-center gap-6 pt-2 border-t border-gray-50">
        <div className="flex items-center gap-1.5 text-gray-400 hover:text-[#3D348B] transition-colors cursor-pointer group">
          <ArrowBigUp className="w-5 h-5 group-hover:fill-[#3D348B]/10" />
          <span className="text-xs font-bold">{post.vote_count || 0}</span>
        </div>
        <div 
          onClick={() => setShowComments(!showComments)}
          className="flex items-center gap-1.5 text-gray-400 hover:text-[#3D348B] transition-colors cursor-pointer"
        >
          <MessageSquare className="w-5 h-5" />
          <span className="text-xs font-bold">{post.comment_count || 0}</span>
        </div>
        <form onSubmit={handleCommentSubmit} className="flex-1 flex gap-2">
          <input 
            type="text" 
            placeholder="Write your comment"
            className="flex-1 bg-gray-50 rounded-xl px-4 py-2 text-xs outline-none border-none focus:ring-0"
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
          />
          <button 
            type="submit"
            disabled={submittingComment || !newComment.trim()}
            className="text-[#7678ED] hover:text-[#3D348B] transition-colors disabled:opacity-30"
          >
            {submittingComment ? <Loader2 className="w-4 h-4 animate-spin" /> : <Send className="w-4 h-4" />}
          </button>
        </form>
        <button className="text-gray-300 hover:text-[#7678ED] transition-colors">
          <Share2 className="w-5 h-5" />
        </button>
      </div>

      {showComments && (
        <div className="mt-4 pt-4 border-t border-gray-50 flex flex-col gap-4">
          {loadingComments ? (
            <div className="flex justify-center py-2">
              <Loader2 className="w-4 h-4 animate-spin text-[#3D348B]" />
            </div>
          ) : (
            <div className="flex flex-col gap-4">
              {comments.length > 0 ? (
                comments.map((comment) => {
                  const cFirstName = comment.teacher_id ? comment.teacher_first : comment.student_first;
                  const cLastName = comment.teacher_id ? comment.teacher_last : comment.student_last;
                  const cInitial = cFirstName ? cFirstName[0] : (cLastName ? cLastName[0] : '?');
                  
                  return (
                    <div key={comment.id} className="flex gap-3">
                      <div className="w-8 h-8 rounded-lg bg-[#7678ED]/20 flex items-center justify-center text-[#3D348B] font-bold text-xs shrink-0">
                        {cInitial}
                      </div>
                      <div className="flex-1 bg-gray-50 rounded-2xl p-3">
                        <div className="flex justify-between items-center mb-1">
                          <p className="text-[11px] font-bold text-[#3D348B]">
                            {cFirstName} {cLastName}
                          </p>
                          <p className="text-[9px] text-gray-400">
                            {getRelativeTime(comment.created_at || new Date().toISOString())}
                          </p>
                        </div>
                        <p className="text-xs text-[#404359] leading-relaxed">
                          {comment.content}
                        </p>
                      </div>
                    </div>
                  );
                })
              ) : (
                <p className="text-center text-[11px] text-gray-400">No comments yet. Be the first to comment!</p>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

'use client'

import React, { useRef } from 'react';
import { Image as ImageIcon, Video, FileText, Smile, Loader2 } from 'lucide-react';

interface PostComposerProps {
  newPostContent: string;
  setNewPostContent: (content: string) => void;
  selectedImage: string | null;
  setSelectedImage: (image: string | null) => void;
  submitting: boolean;
  onPublish: () => void;
}

export default function PostComposer({
  newPostContent,
  setNewPostContent,
  selectedImage,
  setSelectedImage,
  submitting,
  onPublish
}: PostComposerProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleImageSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => setSelectedImage(reader.result as string);
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className="bg-white rounded-[24px] p-6 border border-gray-100 flex flex-col gap-4">
      <div className="flex gap-4">
        <div className="w-12 h-12 rounded-2xl bg-[#7678ED] shrink-0 flex items-center justify-center text-white font-bold">
          J
        </div>
        <input 
          type="text" 
          placeholder="What's on your mind?"
          className="flex-1 bg-gray-50 rounded-2xl px-6 text-sm outline-none border-none focus:ring-0 placeholder-gray-400"
          value={newPostContent}
          onChange={(e) => setNewPostContent(e.target.value)}
        />
      </div>
      
      {selectedImage && (
        <div className="relative w-full rounded-2xl overflow-hidden bg-gray-100 max-h-[300px]">
          <img src={selectedImage} alt="selected" className="w-full h-full object-contain" />
          <button 
            onClick={() => setSelectedImage(null)}
            className="absolute top-2 right-2 p-1 bg-black/50 text-white rounded-full hover:bg-black/70"
          >
            <ImageIcon className="w-4 h-4 rotate-45" />
          </button>
        </div>
      )}

      <div className="flex justify-between items-center pt-2">
        <div className="flex gap-5">
          <button onClick={() => fileInputRef.current?.click()} className="flex items-center gap-2 text-gray-400 hover:text-green-500 transition-colors">
            <ImageIcon className="w-5 h-5 text-green-500" />
            <span className="text-xs font-medium">Photo</span>
          </button>
          <button className="flex items-center gap-2 text-gray-400 hover:text-pink-500 transition-colors">
            <Video className="w-5 h-5 text-pink-500" />
            <span className="text-xs font-medium">Video</span>
          </button>
          <button className="flex items-center gap-2 text-gray-400 hover:text-blue-500 transition-colors">
            <FileText className="w-5 h-5 text-blue-500" />
            <span className="text-xs font-medium">File</span>
          </button>
          <button className="flex items-center gap-2 text-gray-400 hover:text-yellow-500 transition-colors">
            <Smile className="w-5 h-5 text-yellow-500" />
            <span className="text-xs font-medium">Feeling</span>
          </button>
        </div>
        <input type="file" ref={fileInputRef} hidden accept="image/*" onChange={handleImageSelect} />
        <button 
          disabled={submitting || (!newPostContent.trim() && !selectedImage)}
          onClick={onPublish}
          className="px-8 py-2.5 bg-[#3D348B] text-white text-sm font-bold rounded-xl hover:bg-[#2d2570] disabled:opacity-50 transition-all flex items-center gap-2"
        >
          {submitting && <Loader2 className="w-4 h-4 animate-spin" />}
          Publich post
        </button>
      </div>
    </div>
  );
}

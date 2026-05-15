import React, { useState, useEffect } from 'react';
import { Loader2 } from 'lucide-react';
import { getGlobalFeed, createGlobalPost } from '@/services/workspace';
import ProfileCard from '@/components/home/ProfileCard';
import CommunitiesCard from '@/components/home/CommunitiesCard';
import PostComposer from '@/components/home/PostComposer';
import PostCard from '@/components/home/PostCard';
import EventsCard from '@/components/home/EventsCard';
import PeopleCard from '@/components/home/PeopleCard';

// Helper for relative time
const getRelativeTime = (dateStr: string) => {
  const date = new Date(dateStr);
  const now = new Date();
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);
  
  if (diffInSeconds < 60) return 'Just now';
  const diffInMinutes = Math.floor(diffInSeconds / 60);
  if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
  const diffInHours = Math.floor(diffInMinutes / 60);
  if (diffInHours < 24) return `${diffInHours}h ago`;
  const diffInDays = Math.floor(diffInHours / 24);
  return `${diffInDays}d ago`;
};

const Home = () => {
  const [posts, setPosts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [newPostContent, setNewPostContent] = useState('');
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchFeed();
  }, []);

  const fetchFeed = async () => {
    setLoading(true);
    try {
      const data = await getGlobalFeed();
      setPosts(data);
    } catch (error) {
      console.error('Failed to fetch feed:', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePublishPost = async () => {
    if (!newPostContent.trim() && !selectedImage) return;
    setSubmitting(true);
    try {
      await createGlobalPost({
        content: newPostContent,
        image: selectedImage || undefined
      });
      setNewPostContent('');
      setSelectedImage(null);
      fetchFeed();
    } catch (error) {
      console.error('Failed to publish post:', error);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="w-full h-full">
      <div className="mx-auto grid grid-cols-1 lg:grid-cols-12 gap-8 pb-12">
        
        {/* Left Sidebar */}
        <div className="lg:col-span-3 flex flex-col gap-6 lg:sticky lg:top-0 lg:self-start z-10">
          <ProfileCard />
          <CommunitiesCard />
        </div>

        {/* Main Feed */}
        <div className="lg:col-span-6 flex flex-col gap-6">
          <PostComposer 
            newPostContent={newPostContent}
            setNewPostContent={setNewPostContent}
            selectedImage={selectedImage}
            setSelectedImage={setSelectedImage}
            submitting={submitting}
            onPublish={handlePublishPost}
          />

          {/* Posts Feed */}
          {loading ? (
            <div className="flex justify-center py-20">
              <Loader2 className="w-10 h-10 animate-spin text-[#3D348B]" />
            </div>
          ) : (
            <div className="flex flex-col gap-6">
              {posts.map((post) => (
                <PostCard key={post.id} post={post} getRelativeTime={getRelativeTime} />
              ))}
            </div>
          )}
        </div>

        {/* Right Sidebar */}
        <div className="lg:col-span-3 flex flex-col gap-6 lg:sticky lg:top-0 lg:self-start z-10">
          <EventsCard />
          <PeopleCard />
        </div>

      </div>
    </div>
  );
};

export default Home;
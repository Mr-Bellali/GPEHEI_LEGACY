'use client'

import React, { useState, useEffect } from 'react';
import { ChevronLeft, Users, FileText, Calendar, Send, Plus, Loader2, MoreVertical } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { getFlow, getAssignments, createPost, createCourse, createHomework } from '@/services/workspace';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

interface WorkspaceDetailProps {
    module: {
        module_id: number;
        module_name: string;
        semester: number;
        name_filier: string;
    };
    group: {
        id: number;
        group_name: string;
        filiere_name: string;
    };
    onBack: () => void;
}

type Tab = 'flow' | 'assignments';

interface ModalProps {
    isOpen: boolean;
    onClose: () => void;
    title: string;
    children: React.ReactNode;
}

const Modal = ({ isOpen, onClose, title, children }: ModalProps) => {
    if (!isOpen) return null;
    return (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-3xl w-full max-w-2xl shadow-2xl overflow-hidden">
                <div className="px-8 py-6 border-b border-gray-100 flex justify-between items-center">
                    <h2 className="text-2xl font-bold text-[#3D348B]">{title}</h2>
                    <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-full text-gray-400">
                        <Plus className="w-6 h-6 rotate-45" />
                    </button>
                </div>
                <div className="p-8">
                    {children}
                </div>
            </div>
        </div>
    );
};

export default function WorkspaceDetail({ module, group, onBack }: WorkspaceDetailProps) {
    const [activeTab, setActiveTab] = useState<Tab>('flow');
    const [loading, setLoading] = useState(true);
    const [flowData, setFlowData] = useState<any[]>([]);
    const [assignmentsData, setAssignmentsData] = useState<any[]>([]);
    const [newPost, setNewPost] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [selectedAssignment, setSelectedAssignment] = useState<any>(null);

    // Modals state
    const [isCourseModalOpen, setIsCourseModalOpen] = useState(false);
    const [isHomeworkModalOpen, setIsHomeworkModalOpen] = useState(false);
    const [formData, setFormData] = useState({ title: '', content: '', deadline: '' });

    useEffect(() => {
        fetchData();
    }, [activeTab]);

    const fetchData = async () => {
        setLoading(true);
        try {
            if (activeTab === 'flow') {
                const data = await getFlow(module.module_id, group.id);
                setFlowData(data);
            } else {
                const data = await getAssignments(module.module_id, group.id);
                setAssignmentsData(data);
                if (data.length > 0 && !selectedAssignment) {
                    setSelectedAssignment(data[0]);
                }
            }
        } catch (error) {
            console.error('Failed to fetch data:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleCreatePost = async () => {
        if (!newPost.trim()) return;
        setSubmitting(true);
        try {
            await createPost({
                title: 'Announcement',
                content: newPost,
                module_id: module.module_id,
                groupe_id: group.id
            });
            setNewPost('');
            fetchData();
        } catch (error) {
            console.error('Failed to create post:', error);
        } finally {
            setSubmitting(false);
        }
    };

    const handleCreateCourse = async (e: React.FormEvent) => {
        e.preventDefault();
        setSubmitting(true);
        try {
            await createCourse({
                title: formData.title,
                content: formData.content,
                module_id: module.module_id
            });
            setIsCourseModalOpen(false);
            setFormData({ title: '', content: '', deadline: '' });
            fetchData();
        } catch (error) {
            console.error('Failed to create course:', error);
        } finally {
            setSubmitting(false);
        }
    };

    const handleCreateHomework = async (e: React.FormEvent) => {
        e.preventDefault();
        setSubmitting(true);
        try {
            await createHomework({
                title: formData.title,
                content: formData.content,
                deadline: formData.deadline,
                module_id: module.module_id,
                groupe_id: group.id
            });
            setIsHomeworkModalOpen(false);
            setFormData({ title: '', content: '', deadline: '' });
            fetchData();
        } catch (error) {
            console.error('Failed to create homework:', error);
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="flex flex-col h-full bg-[#F0F0F0]">
            {/* Header / Banner */}
            <div className="bg-[#7678ED] rounded-2xl p-8 mb-6 relative overflow-hidden">
                <button 
                    onClick={onBack}
                    className="absolute top-4 left-4 p-2 bg-white/20 hover:bg-white/30 rounded-full text-white transition-colors"
                >
                    <ChevronLeft className="w-6 h-6" />
                </button>
                <div className="relative z-10 mt-4 flex justify-between items-end">
                    <div>
                        <h1 className="text-4xl font-bold text-white mb-2">{module.module_name}</h1>
                        <p className="text-white/80 text-lg">
                            {module.semester} {module.name_filier}
                            <br />
                            Groupe {group.group_name}
                        </p>
                    </div>
                    {/* Workflow button requested by user */}
                    <button className="px-6 py-2 bg-white text-[#7678ED] rounded-full font-semibold hover:bg-gray-100 transition-colors shadow-lg">
                        Workflow
                    </button>
                </div>
            </div>

            {/* Tabs */}
            <div className="flex justify-center mb-6">
                <div className="bg-white/50 p-1 rounded-full flex gap-1 shadow-sm">
                    <button
                        onClick={() => setActiveTab('flow')}
                        className={cn(
                            "px-8 py-2 rounded-full text-sm font-medium transition-all",
                            activeTab === 'flow' ? "bg-[#3D348B] text-white shadow-md" : "text-[#404359] hover:bg-white/50"
                        )}
                    >
                        Flow
                    </button>
                    <button
                        onClick={() => setActiveTab('assignments')}
                        className={cn(
                            "px-8 py-2 rounded-full text-sm font-medium transition-all",
                            activeTab === 'assignments' ? "bg-[#3D348B] text-white shadow-md" : "text-[#404359] hover:bg-white/50"
                        )}
                    >
                        Assignments and courses
                    </button>
                </div>
            </div>

            {/* Content Area */}
            <div className="flex-1 overflow-y-auto pb-10 px-4">
                {loading ? (
                    <div className="flex items-center justify-center py-20">
                        <Loader2 className="w-10 h-10 animate-spin text-[#3D348B]" />
                    </div>
                ) : activeTab === 'flow' ? (
                    <div className="max-w-4xl mx-auto flex flex-col gap-6">
                        {/* Create Post Card */}
                        <div className="bg-white rounded-2xl p-6 shadow-sm">
                            <div className="flex gap-4">
                                <div className="w-10 h-10 rounded-full bg-[#7678ED] flex-shrink-0" />
                                <div className="flex-1 flex flex-col gap-3">
                                    <textarea
                                        placeholder="Announce something to your class"
                                        className="w-full bg-gray-50 rounded-xl p-4 text-sm border-none focus:ring-2 focus:ring-[#7678ED] transition-all min-h-[100px] resize-none"
                                        value={newPost}
                                        onChange={(e) => setNewPost(e.target.value)}
                                    />
                                    <div className="flex justify-end gap-2">
                                        <button 
                                            disabled={submitting || !newPost.trim()}
                                            onClick={handleCreatePost}
                                            className="px-6 py-2 bg-[#3D348B] text-white rounded-full text-sm font-medium hover:bg-[#2d2570] disabled:opacity-50 transition-all flex items-center gap-2"
                                        >
                                            {submitting ? <Loader2 className="w-4 h-4 animate-spin" /> : <Send className="w-4 h-4" />}
                                            Post
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Posts List */}
                        {flowData.length > 0 ? (
                            flowData.map((post) => (
                                <div key={post.id} className="bg-white rounded-2xl p-6 shadow-sm border border-transparent hover:border-[#7678ED]/30 transition-all">
                                    <div className="flex justify-between items-start mb-4">
                                        <div className="flex gap-4 items-center">
                                            <div className="w-10 h-10 rounded-full bg-[#7678ED] flex items-center justify-center text-white font-bold">
                                                {post.first_name?.[0]}{post.last_name?.[0]}
                                            </div>
                                            <div>
                                                <h3 className="font-semibold text-[#3D348B]">{post.first_name} {post.last_name}</h3>
                                                <p className="text-xs text-gray-400">21 April</p>
                                            </div>
                                        </div>
                                        <button className="text-gray-400 hover:text-gray-600">
                                            <MoreVertical className="w-5 h-5" />
                                        </button>
                                    </div>
                                    <div className="prose prose-sm max-w-none text-[#404359]">
                                        <ReactMarkdown remarkPlugins={[remarkGfm]}>
                                            {post.content}
                                        </ReactMarkdown>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <div className="text-center py-20 text-gray-500">
                                <p>No announcements yet.</p>
                            </div>
                        )}
                    </div>
                ) : (
                    <div className="flex gap-8 max-w-6xl mx-auto">
                        {/* Sidebar Categories */}
                        <div className="w-64 flex-shrink-0 flex flex-col gap-8">
                            <div>
                                <h3 className="text-[#3D348B] font-bold mb-4 flex items-center gap-2">
                                    <div className="w-1 h-6 bg-[#3D348B] rounded-full" />
                                    Courses
                                </h3>
                                <div className="flex flex-col gap-2">
                                    {assignmentsData.filter(a => a.type === 'course').map(course => (
                                        <div 
                                            key={course.id} 
                                            onClick={() => setSelectedAssignment(course)}
                                            className={cn(
                                                "bg-white p-3 rounded-xl shadow-sm border-l-4 flex items-center gap-3 cursor-pointer transition-all",
                                                selectedAssignment?.id === course.id && selectedAssignment?.type === 'course' 
                                                    ? "border-[#3D348B] bg-gray-50 scale-[1.02]" 
                                                    : "border-transparent hover:border-[#3D348B]/30"
                                            )}
                                        >
                                            <div className="w-8 h-8 rounded-lg bg-[#3D348B]/10 flex items-center justify-center">
                                                <FileText className="w-4 h-4 text-[#3D348B]" />
                                            </div>
                                            <div className="flex-1 overflow-hidden">
                                                <p className="text-sm font-medium text-[#404359] truncate">{course.title}</p>
                                                <p className="text-[10px] text-gray-400">Added 12 sep 2025</p>
                                            </div>
                                        </div>
                                    ))}
                                    <button 
                                        onClick={() => setIsCourseModalOpen(true)}
                                        className="mt-2 flex items-center gap-2 text-[#3D348B] text-sm font-medium hover:underline"
                                    >
                                        <Plus className="w-4 h-4" /> Add Course
                                    </button>
                                </div>
                            </div>

                            <div>
                                <h3 className="text-[#7678ED] font-bold mb-4 flex items-center gap-2">
                                    <div className="w-1 h-6 bg-[#7678ED] rounded-full" />
                                    TP
                                </h3>
                                <div className="flex flex-col gap-2">
                                    {assignmentsData.filter(a => a.type === 'homework').map(hw => (
                                        <div 
                                            key={hw.id} 
                                            onClick={() => setSelectedAssignment(hw)}
                                            className={cn(
                                                "bg-white p-3 rounded-xl shadow-sm border-l-4 flex items-center gap-3 cursor-pointer transition-all",
                                                selectedAssignment?.id === hw.id && selectedAssignment?.type === 'homework' 
                                                    ? "border-[#7678ED] bg-gray-50 scale-[1.02]" 
                                                    : "border-transparent hover:border-[#7678ED]/30"
                                            )}
                                        >
                                            <div className="w-8 h-8 rounded-lg bg-[#7678ED]/10 flex items-center justify-center">
                                                <Calendar className="w-4 h-4 text-[#7678ED]" />
                                            </div>
                                            <div className="flex-1 overflow-hidden">
                                                <p className="text-sm font-medium text-[#404359] truncate">{hw.title}</p>
                                                <div className="flex justify-between items-center">
                                                    <p className="text-[10px] text-gray-400">Due {hw.deadline}</p>
                                                    <span className="text-[10px] text-[#7678ED] font-semibold">Assigned</span>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                    <button 
                                        onClick={() => setIsHomeworkModalOpen(true)}
                                        className="mt-2 flex items-center gap-2 text-[#7678ED] text-sm font-medium hover:underline"
                                    >
                                        <Plus className="w-4 h-4" /> Add Assignment
                                    </button>
                                </div>
                            </div>
                        </div>

                        {/* Main Assignment Content */}
                        <div className="flex-1 bg-white rounded-3xl p-8 shadow-sm min-h-[600px]">
                            {selectedAssignment ? (
                                <div>
                                    <div className="flex justify-between items-center mb-6">
                                        <h2 className="text-2xl font-bold text-[#3D348B]">{selectedAssignment.title}</h2>
                                        <span className="text-xs text-gray-400">Added 12 sep 2025</span>
                                    </div>
                                    <div className="prose prose-sm max-w-none text-[#404359]">
                                        <ReactMarkdown remarkPlugins={[remarkGfm]}>
                                            {selectedAssignment.content}
                                        </ReactMarkdown>
                                    </div>
                                </div>
                            ) : (
                                <div className="flex flex-col items-center justify-center h-full text-gray-400">
                                    <FileText className="w-16 h-16 mb-4 opacity-20" />
                                    <p>Select an item to view content</p>
                                </div>
                            )}
                        </div>
                    </div>
                )}
            </div>

            {/* Course Modal */}
            <Modal 
                isOpen={isCourseModalOpen} 
                onClose={() => setIsCourseModalOpen(false)} 
                title="Create New Course"
            >
                <form onSubmit={handleCreateCourse} className="flex flex-col gap-6">
                    <div className="flex flex-col gap-2">
                        <label className="text-sm font-semibold text-[#404359]">Course Title</label>
                        <input 
                            required
                            type="text" 
                            className="w-full bg-gray-50 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-[#3D348B] outline-none transition-all"
                            placeholder="e.g. Introduction to Derivatives"
                            value={formData.title}
                            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                        />
                    </div>
                    <div className="flex flex-col gap-2">
                        <label className="text-sm font-semibold text-[#404359]">Content (Markdown supported)</label>
                        <textarea 
                            required
                            className="w-full bg-gray-50 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-[#3D348B] outline-none transition-all min-h-[200px] resize-none"
                            placeholder="Write your course content here..."
                            value={formData.content}
                            onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                        />
                    </div>
                    <button 
                        disabled={submitting}
                        className="w-full bg-[#3D348B] text-white py-4 rounded-2xl font-bold hover:bg-[#2d2570] transition-all disabled:opacity-50"
                    >
                        {submitting ? 'Creating...' : 'Create Course'}
                    </button>
                </form>
            </Modal>

            {/* Homework Modal */}
            <Modal 
                isOpen={isHomeworkModalOpen} 
                onClose={() => setIsHomeworkModalOpen(false)} 
                title="Assign New Homework"
            >
                <form onSubmit={handleCreateHomework} className="flex flex-col gap-6">
                    <div className="flex flex-col gap-2">
                        <label className="text-sm font-semibold text-[#404359]">Homework Title</label>
                        <input 
                            required
                            type="text" 
                            className="w-full bg-gray-50 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-[#7678ED] outline-none transition-all"
                            placeholder="e.g. Exercise Sheet 1"
                            value={formData.title}
                            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                        />
                    </div>
                    <div className="flex flex-col gap-2">
                        <label className="text-sm font-semibold text-[#404359]">Instructions (Markdown supported)</label>
                        <textarea 
                            required
                            className="w-full bg-gray-50 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-[#7678ED] outline-none transition-all min-h-[150px] resize-none"
                            placeholder="Write instructions for the students..."
                            value={formData.content}
                            onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                        />
                    </div>
                    <div className="flex flex-col gap-2">
                        <label className="text-sm font-semibold text-[#404359]">Deadline</label>
                        <input 
                            required
                            type="datetime-local" 
                            className="w-full bg-gray-50 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-[#7678ED] outline-none transition-all"
                            value={formData.deadline}
                            onChange={(e) => setFormData({ ...formData, deadline: e.target.value })}
                        />
                    </div>
                    <button 
                        disabled={submitting}
                        className="w-full bg-[#7678ED] text-white py-4 rounded-2xl font-bold hover:bg-[#5a5cd6] transition-all disabled:opacity-50"
                    >
                        {submitting ? 'Assigning...' : 'Assign Homework'}
                    </button>
                </form>
            </Modal>
        </div>
    );
}

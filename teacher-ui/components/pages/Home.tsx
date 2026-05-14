import React from 'react';
import { BookOpen, Users, Calendar, ArrowRight, Bell, CheckCircle, Clock } from 'lucide-react';

const StatCard = ({ icon: Icon, label, value, color }: any) => (
  <div className="bg-white rounded-3xl p-6 shadow-sm border border-transparent hover:border-gray-200 transition-all">
    <div className={`w-12 h-12 ${color} rounded-2xl flex items-center justify-center mb-4`}>
      <Icon className="w-6 h-6 text-white" />
    </div>
    <p className="text-sm text-gray-500 font-medium">{label}</p>
    <h3 className="text-2xl font-bold text-[#3D348B]">{value}</h3>
  </div>
);

const Home = () => {
  const stats = [
    { icon: BookOpen, label: 'Active Modules', value: '4', color: 'bg-[#3D348B]' },
    { icon: Users, label: 'Total Students', value: '124', color: 'bg-[#7678ED]' },
    { icon: Calendar, label: 'Pending Assignments', value: '8', color: 'bg-[#F35B04]' },
    { icon: CheckCircle, label: 'Submissions Today', value: '32', color: 'bg-green-500' },
  ];

  const recentActivities = [
    { id: 1, type: 'submission', text: 'Ahmed Ali submitted "Calculus HW1"', time: '2 hours ago', icon: CheckCircle, iconColor: 'text-green-500' },
    { id: 2, type: 'alert', text: 'New announcement posted in "Advanced Algebra"', time: '4 hours ago', icon: Bell, iconColor: 'text-[#7678ED]' },
    { id: 3, type: 'deadline', text: 'Deadline approaching for "Physics Lab Report"', time: 'Tomorrow', icon: Clock, iconColor: 'text-[#F35B04]' },
  ];

  return (
    <div className="flex flex-col gap-8 max-w-7xl mx-auto px-4 pb-12">
      {/* Welcome Section */}
      <div className="bg-[#3D348B] rounded-[40px] p-10 relative overflow-hidden text-white">
        <div className="relative z-10">
          <h1 className="text-4xl font-bold mb-4">Welcome back, Professor!</h1>
          <p className="text-white/70 text-lg max-w-md">
            You have 8 pending assignments to grade and 2 new messages in your inbox.
          </p>
          <button className="mt-8 px-8 py-3 bg-[#7678ED] text-white rounded-full font-bold hover:bg-[#5a5cd6] transition-all flex items-center gap-2">
            View Schedule <ArrowRight className="w-5 h-5" />
          </button>
        </div>
        <div className="absolute right-[-20px] bottom-[-20px] w-64 h-64 bg-white/10 rounded-full blur-3xl" />
        <div className="absolute right-[100px] top-[-50px] w-48 h-48 bg-[#7678ED]/20 rounded-full blur-2xl" />
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, i) => (
          <StatCard key={i} {...stat} />
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Recent Activity */}
        <div className="lg:col-span-2 flex flex-col gap-6">
          <h2 className="text-2xl font-bold text-[#3D348B]">Recent Activity</h2>
          <div className="bg-white rounded-[32px] p-8 shadow-sm flex flex-col gap-6">
            {recentActivities.map((activity) => (
              <div key={activity.id} className="flex items-center justify-between group cursor-pointer">
                <div className="flex items-center gap-4">
                  <div className={`w-10 h-10 rounded-xl bg-gray-50 flex items-center justify-center`}>
                    <activity.icon className={`w-5 h-5 ${activity.iconColor}`} />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-[#404359] group-hover:text-[#3D348B] transition-colors">{activity.text}</p>
                    <p className="text-xs text-gray-400">{activity.time}</p>
                  </div>
                </div>
                <ArrowRight className="w-4 h-4 text-gray-300 group-hover:text-[#3D348B] transition-all" />
              </div>
            ))}
          </div>
        </div>

        {/* Quick Links / Tasks */}
        <div className="flex flex-col gap-6">
          <h2 className="text-2xl font-bold text-[#3D348B]">Quick Tasks</h2>
          <div className="bg-white rounded-[32px] p-8 shadow-sm flex flex-col gap-4">
            {['Grade HW2', 'Prepare Lecture 5', 'Update Syllabus'].map((task, i) => (
              <label key={i} className="flex items-center gap-3 p-3 hover:bg-gray-50 rounded-xl cursor-pointer transition-colors">
                <input type="checkbox" className="w-5 h-5 rounded-md border-gray-300 text-[#3D348B] focus:ring-[#3D348B]" />
                <span className="text-sm font-medium text-[#404359]">{task}</span>
              </label>
            ))}
            <button className="mt-2 text-sm font-bold text-[#7678ED] hover:underline text-left">+ Add new task</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
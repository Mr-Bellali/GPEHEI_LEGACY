import React, { useState } from 'react';
import { Search, FileText, Download, MoreVertical, Plus, Filter, LayoutGrid, List } from 'lucide-react';

const Library = () => {
  const [view, setView] = useState<'grid' | 'list'>('grid');
  
  const documents = [
    { id: 1, name: 'Course_Syllabus_2024.pdf', type: 'PDF', size: '2.4 MB', date: '12 May 2024' },
    { id: 2, name: 'Derivatives_Lecture_Notes.docx', type: 'DOCX', size: '1.1 MB', date: '10 May 2024' },
    { id: 3, name: 'Final_Exam_Template.pdf', type: 'PDF', size: '850 KB', date: '05 May 2024' },
    { id: 4, name: 'Student_Grades_S1.xlsx', type: 'XLSX', size: '450 KB', date: '01 May 2024' },
    { id: 5, name: 'Physics_Project_Guide.pdf', type: 'PDF', size: '3.2 MB', date: '28 Apr 2024' },
  ];

  return (
    <div className="flex flex-col gap-8 max-w-7xl mx-auto px-4 pb-12">
      {/* Library Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-[#3D348B]">Digital Library</h1>
          <p className="text-gray-500">Manage your documents and resources</p>
        </div>
        <button className="px-6 py-3 bg-[#3D348B] text-white rounded-full font-bold hover:bg-[#2d2570] transition-all flex items-center gap-2 shadow-lg">
          <Plus className="w-5 h-5" /> Upload Document
        </button>
      </div>

      {/* Toolbar */}
      <div className="bg-white rounded-3xl p-4 shadow-sm flex flex-col sm:flex-row justify-between items-center gap-4">
        <div className="flex items-center bg-gray-50 rounded-2xl px-4 py-2 w-full sm:w-96 gap-3">
          <Search className="w-5 h-5 text-gray-400" />
          <input 
            type="text" 
            placeholder="Search documents..." 
            className="bg-transparent border-none outline-none text-sm w-full"
          />
        </div>
        <div className="flex items-center gap-2">
          <button className="p-2 hover:bg-gray-100 rounded-xl transition-colors">
            <Filter className="w-5 h-5 text-gray-500" />
          </button>
          <div className="h-6 w-[1px] bg-gray-200 mx-2" />
          <button 
            onClick={() => setView('grid')}
            className={`p-2 rounded-xl transition-all ${view === 'grid' ? 'bg-[#7678ED]/10 text-[#7678ED]' : 'text-gray-400 hover:bg-gray-100'}`}
          >
            <LayoutGrid className="w-5 h-5" />
          </button>
          <button 
            onClick={() => setView('list')}
            className={`p-2 rounded-xl transition-all ${view === 'list' ? 'bg-[#7678ED]/10 text-[#7678ED]' : 'text-gray-400 hover:bg-gray-100'}`}
          >
            <List className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Documents Grid/List */}
      {view === 'grid' ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 xl:grid-cols-5 gap-6">
          {documents.map((doc) => (
            <div key={doc.id} className="bg-white rounded-3xl p-5 shadow-sm hover:shadow-xl transition-all group border border-transparent hover:border-[#7678ED]/20">
              <div className="flex justify-between items-start mb-4">
                <div className="w-12 h-12 bg-gray-50 rounded-2xl flex items-center justify-center text-[#7678ED] group-hover:bg-[#7678ED] group-hover:text-white transition-all">
                  <FileText className="w-6 h-6" />
                </div>
                <button className="text-gray-300 hover:text-gray-600">
                  <MoreVertical className="w-5 h-5" />
                </button>
              </div>
              <h3 className="font-semibold text-[#3D348B] text-sm mb-1 truncate">{doc.name}</h3>
              <p className="text-[11px] text-gray-400 mb-4">{doc.size} • {doc.date}</p>
              <button className="w-full py-2 bg-gray-50 text-[#7678ED] rounded-xl text-xs font-bold hover:bg-[#7678ED] hover:text-white transition-all flex items-center justify-center gap-2">
                <Download className="w-3 h-3" /> Download
              </button>
            </div>
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-3xl shadow-sm overflow-hidden">
          <table className="w-full text-left">
            <thead className="bg-gray-50 text-[#404359] text-sm font-bold">
              <tr>
                <th className="px-6 py-4">Name</th>
                <th className="px-6 py-4">Type</th>
                <th className="px-6 py-4">Size</th>
                <th className="px-6 py-4">Date</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {documents.map((doc) => (
                <tr key={doc.id} className="hover:bg-gray-50 transition-colors group">
                  <td className="px-6 py-4 flex items-center gap-3">
                    <FileText className="w-5 h-5 text-[#7678ED]" />
                    <span className="text-sm font-medium text-[#404359]">{doc.name}</span>
                  </td>
                  <td className="px-6 py-4 text-xs text-gray-500 font-bold">{doc.type}</td>
                  <td className="px-6 py-4 text-xs text-gray-400">{doc.size}</td>
                  <td className="px-6 py-4 text-xs text-gray-400">{doc.date}</td>
                  <td className="px-6 py-4 text-right">
                    <button className="p-2 text-gray-400 hover:text-[#3D348B] transition-colors">
                      <Download className="w-5 h-5" />
                    </button>
                    <button className="p-2 text-gray-400 hover:text-red-500 transition-colors">
                      <MoreVertical className="w-5 h-5" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default Library;
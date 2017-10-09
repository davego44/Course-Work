/***********************************************************************
* Component:
*    Scheduler SRT
* Author:
*    David Lobaccaro
* Summary: 
*    This is the base-class that enables various schedule algorithms
*    to simulate CPU Scheduling
************************************************************************/

#ifndef SCHEDULER_SRT
#define SCHEDULER_SRT

#include "schedule.h"
#include <algorithm>

/****************************************************
 * SRT
 * The Shortest Remaining Time Scheduler
 ***************************************************/
class SchedulerSRT : public Disbatcher
{
public:
   SchedulerSRT() : Disbatcher() {}

   // a new process has just been executed
   void startProcess(int pid)
   {
      pids.push_back(pid); //Add the new process to the list
   }
   
   // execute one clock cycle
   bool clock()
   {
      if (pidCurrent != PID_NONE && processes[pidCurrent].isDone()) //if a process is selected and it is done
         pids.erase(remove(pids.begin(), pids.end(), pidCurrent), pids.end()); //remove it from the list
      if (pids.size()) 
         pidCurrent = getPid(); //get the appropraite pid for SRT
      else
         pidCurrent = PID_NONE; //set to unselected
      return Disbatcher::clock();
   }

   // returns the pid associated with the process that has the SRT
   int getPid()
   {
      vector<int>::iterator it, srtIt;
      it = pids.begin();
      srtIt = pids.begin();
      for (; it != pids.end(); ++it)
      {
         if (processes[*it].getTimeLeft() < //compare times and get the smaller one
             processes[*srtIt].getTimeLeft())
            srtIt = it;
      }
      return *srtIt;
   }

  private:
   vector<int> pids; //list of process pids
};

#endif // SCHEDULE_SRT

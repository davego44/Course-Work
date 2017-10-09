/***********************************************************************
* Component:
*    Scheduler SJF
* Author:
*    David Lobaccaro
* Summary: 
*    This is the base-class that enables various schedule algorithms
*    to simulate CPU Scheduling
************************************************************************/

#ifndef SCHEDULER_SJF
#define SCHEDULER_SJF

#include <map>
#include "schedule.h"

using namespace std;

/****************************************************
 * SJF
 * The Shortest Job First scheduler
 ***************************************************/
class SchedulerSJF : public Disbatcher
{
public:
  SchedulerSJF() : Disbatcher() {}

   // a new process has just been executed
   void startProcess(int pid)
   {
      pids.push_back(pid); //Add new process pid to the vector
   }
   
   
   // execute one clock cycle
   bool clock()
   {
      if (pidCurrent == PID_NONE || //if no process is selected
          processes[pidCurrent].isDone()) //or if the current process is done
      {
         if (pids.size()) //if the vector is not empty      
            pidCurrent = getPID(); //grab the appropriate pid
         else
            pidCurrent = PID_NONE; //set it to unselected
      }
      return Disbatcher::clock();
   }

   //returns the smallest service time process pid
   int getPID()
   {
      vector<int>::iterator it;
      vector<int>::iterator smallestIt = pids.begin(); //auto set the smallest to the first
      for (it = pids.begin(); it != pids.end(); it++)
      {
         if (processes[*it].getServiceTime() <
             processes[*smallestIt].getServiceTime()) //if the incrementing one is smaller, use that
            smallestIt = it;
      }
      int ret = *smallestIt; //grab the pid
      pids.erase(smallestIt); //and erase it from the vector
      return ret;
   }

private:
   vector<int> pids; //list of processes
};

#endif // SCHEDULE_SJF

/***********************************************************************
* Component:
*    Scheduler RR
* Author:
*    David Lobaccaro
* Summary: 
*    This is the base-class that enables various schedule algorithms
*    to simulate CPU Scheduling
************************************************************************/

#ifndef SCHEDULER_RR
#define SCHEDULER_RR

#include <queue>
#include <iostream>
#include "schedule.h"

using namespace std;

/****************************************************
 * RR
 * The Round Robin scheduler
 ***************************************************/
class SchedulerRR : public Disbatcher
{
public:
   SchedulerRR(int q) : Disbatcher(),
      timeQuantaDuration(q) { currentTime = q + 1; }

   // a new process has just been executed
   void startProcess(int pid)
   {
      readyQueue.push(pid); //Add the new process to the queue
   }
      
   // execute one clock cycle
   bool clock()
   {
      if (currentTime > timeQuantaDuration || //if the counter triggers it
          processes[pidCurrent].getTimeLeft() == 0) //or if the process is done
      {
         if (pidCurrent != PID_NONE && //if a process is selected
             processes[pidCurrent].getTimeLeft() != 0) //and if there is still time
            readyQueue.push(pidCurrent); //re-add the popped pid
         if (readyQueue.size()) //if the queue isn't empty
         {
            pidCurrent = readyQueue.front(); //get the new element from the queue
            readyQueue.pop(); //get rid of the element
            currentTime = 1; //reset the counter
         }
         else
            currentTime = timeQuantaDuration + 1; //make the counter trigger clock
      }
      currentTime++; //increment the counter
      return Disbatcher::clock();
   }

  private:
   int timeQuantaDuration;
   int currentTime; //counter for triggering the clock
   queue<int> readyQueue;
};

#endif // SCHEDULE_RR

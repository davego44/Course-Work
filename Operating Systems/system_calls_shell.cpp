/***********************************************************************
 * Program:
 *    Lab 03, Shell and History
 *    Bob Ball, CS345
 * Author:
 *    David Lobaccaro
 * Summary:
 *    This program acts like a command shell. It creates child processes
 *    and waits for them to finished depending on the command entered. It
 *    also has a history feature that gets triggered with the SIGQUIT
 *    signal. Therefore it has the code to handle the SIGQUIT signal. It
 *    uses the fork() and execvp() system call.
 *
 *    UPDATE: Well no one commented on my post so I received no feedback.
 *    I am just re-sending my code submitted on Tuesday.
 ************************************************************************/
#include <iostream>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <signal.h>
#include <string.h>
#include <sstream>
#include <iomanip>
#include <algorithm>

#define MAX_LINE 80 /* 80 chars per line, per command, should be enough. */
#define MAX_HISTORY 10 //max number of history entries
#define MAX_COUNT 1000 //max number the history entries can go up to

using namespace std;

string history[MAX_HISTORY]; //holds all the history data
int currentCmdCount = 0; //number of commands entered

/***************************************************************************
 * displayHistory
 *
 * Responsible to loop through each entry in history and display it on the
 * screen.
 **************************************************************************/
void displayHistory()
{
   int index = 0;
   int length;
   write(STDOUT_FILENO, "\n", 1); //start output on a new line
   if (currentCmdCount > MAX_HISTORY)
      index = currentCmdCount - MAX_HISTORY; //setup index correctly
   for (; index < currentCmdCount; ++index) //loop through history
   {
      stringstream ss; //set up stream for formatting
      ss << " " << setw(3) << index << " " << history[index % MAX_HISTORY];
      length = ss.str().size();
      char output[length];
      strncpy(output, ss.str().c_str(), length); //copy to a workable c string
      write(STDOUT_FILENO, output, length); //write the entry
   }
   return;
}

/***************************************************************************
 * handle_SIGQUIT
 *
 * Responsible for catching the ctrl + \ command and executing displayHistory
 **************************************************************************/
void handle_SIGQUIT(int junk)
{
   displayHistory(); //simply display the history
   return;
}

/***************************************************************************
 * addHistory
 *
 * Responsible for adding a string to history with a rotating array
 **************************************************************************/
void addHistory(string command)
{
   //add the command to the right spot
   history[currentCmdCount % MAX_HISTORY] = command;
   currentCmdCount++;
   if (currentCmdCount >= MAX_COUNT)
      currentCmdCount = MAX_HISTORY;
}

/***************************************************************************
 * addHistory (2)
 *
 * Responsible for converting the buffer into a string of revelant data to
 * pass on to the other addHistory function.
 **************************************************************************/
void addHistory(char inputBuffer[], int length)
{
   string input = (string) inputBuffer;
   //only store part of the input buffer that is used for the latest cmd
   addHistory(input.substr(0, length));
}

/***************************************************************************
 * getHistoryCommand
 *
 * Responsible for finding a history entry by the first character. If it is
 * not found, it will return a null string. If the input character c is null
 * it will return the last entry. If there are no entries, it will return
 * a null string.
 **************************************************************************/
string getHistoryCommand(char c)
{
   int index = currentCmdCount;
   string command;
   if (currentCmdCount == 0) //if there are no cmds return null
      return "\0";
   if (c == '\0') //if input is null return the last cmd entered
      return history[(currentCmdCount - 1) % MAX_HISTORY];
   //otherwise search for the cmd that starts with c
   for (; index >= 0 && index != index - MAX_HISTORY; --index)
   {
      command = history[index % MAX_HISTORY];
      if (command[0] == c)
         return command;
   }
   return "\0"; //if nothing is found return null
}

/***************************************************************************
 * SETUP
 * setup() reads in the next command line, separating it into distinct tokens
 * using whitespace as delimiters.
 *
 * setup() modifies the inputBuffer creating a set of null-terminated strings
 * and places pointers into the args[] array that point to the beginning of
 * each argument.  A NULL pointer is placed in the args[] array indicating
 * the end of the argument list.  This is what is needed for using execvp().
 *
 * A ^d input at the beginning of a line, by a user, exits the shell.
 **************************************************************************/
void setup(char inputBuffer[], char * args[], int * background, int length)
{
   //int length; // # of characters in the command line 
   int i;      // loop index for accessing inputBuffer array 
   int start;  // index where beginning of next command parameter is 
   int ct;     // index of where to place the next parameter into args[] 
    
   ct = 0;

   /* read what the user enters on the command line */
   //length = read(STDIN_FILENO, inputBuffer, MAX_LINE);

   start = -1;            /* Used as a flag to indicate that we are looking
                           * for the start of the command or an argument if
                           * it is a -1, or stores the starting position of
                           * the command or argument to be put in args[].
                           */
   if (length == 0)
      exit(0);            /* ^d was entered, end of user command stream */
   if (length < 0)
   {
      perror("error reading the command");
      exit(-1);           /* terminate with error code of -1 */
   }

   /* examine every character in the inputBuffer */
   for (i=0;i<length;i++)
   { 
      switch (inputBuffer[i])
      {
         case ' ':
         case '\t' :               /* argument separators */
            if(start != -1)        /* found the end of the command or arg */
            {
               args[ct] = &inputBuffer[start]; /* set up pointer in args[] */
               ct++;
            }
            inputBuffer[i] = '\0'; /* add a null char; make a C string */
            start = -1;
            break;

         case '\n':                 /* should be the final char examined */
            if (start != -1)        /* if in the command or an argument */
            {
               args[ct] = &inputBuffer[start]; /* set up pointer in args[] */ 
               ct++;
            }
            inputBuffer[i] = '\0';
            args[ct] = NULL; /* no more arguments to this command */
            break;

         default :             /* some other character */
            if (start == -1 && inputBuffer[i] != '&')
               start = i;      /* starting position of the command or arg */
            if (inputBuffer[i] == '&')
            {
               *background  = 1;
               inputBuffer[i] = '\0';
            }
      }  /* end of switch */
   }  /* end of for loop looking at every character */
   args[ct] = NULL; /* just in case the input line was > 80 */
}

/***************************************************************************
 * evaluate
 *
 * Responsible for determining whether of not the command entered was the 'r'
 * command and if it has an argument. It also adds the commands to history.
 **************************************************************************/
bool evaluate(char inputBuffer[], int &length)
{
   //determine if the 'r' cmd was used
   if (inputBuffer[0] == 'r' &&
       (inputBuffer[1] == '\n' || inputBuffer[1] == ' '))
   {
      //it has been used
      string command;
      if (inputBuffer[1] == '\n') //contains no arguments
         command = getHistoryCommand('\0');
      else //has arguments
         command = getHistoryCommand(inputBuffer[2]);
      if (command == "\0") //if return value is null, display error
      {
         cout << "No matching command in history" << endl;
         return false;
      }
      addHistory(command); //add the command to history
      cout << command; //echo the command
      strncpy(inputBuffer, command.c_str(), MAX_LINE); //copy the command into buffer
      length = command.size(); //set the length to the correct value
   }
   else if (inputBuffer[0] != '\n') //if no command, don't add to history
      addHistory(inputBuffer, length); //if 'r'  hasn't been used, just add to history
   return true;
}

/***************************************************************************
 * executeCmd
 *
 * Responsible for creating a child process to execute the command passed in
 * args. It also waits for the child to finish if the background setting is
 * not set to 1.
 **************************************************************************/
bool executeCmd(char *args[], int background)
{
   pid_t pid = fork(); //create another process
   if (pid < 0) //0 if an error happened
   {
      cout << "Error with fork." << endl;
      return false;
   }
   else if (pid == 0) //if child
   {
      if (execvp(args[0], args) == -1) //execute command
         write(STDOUT_FILENO, "Command not found\n", 18); //error happened
   }
   else //if parent
   {
      if (background != 1) //didnt use &
         waitpid(pid, NULL, 0); //therefore must wait for child
   }
   return true;
}

/***************************************************************************
 * setupSigquit
 *
 * Responsible for setting up the system to run the handle_SIGQUIT function
 * when the SIGQUIT signal has been triggered. Also sets the SA_RESTART flag.
 **************************************************************************/
void setupSigquit()
{
   struct sigaction handler;
   handler.sa_handler = handle_SIGQUIT; //set SIGQUIT to correct function
   handler.sa_flags = SA_RESTART; //set flag
   sigaction(SIGQUIT, &handler, NULL); //set it all up
}

/***************************************************************************
 * main
 *
 * Responsible for setting everything else up. Contains the main flow of the
 * program.
 **************************************************************************/
int main(void)
{
   char inputBuffer[MAX_LINE]; /* buffer to hold the command entered */
   int background;             /* equals 1 if a command is followed by '&' */
   char *args[MAX_LINE/2];   /* command line (of 80) has max of 40 arguments */
   int length;

   setupSigquit(); //setup the SIGQUIT handle
   
   while (1)  /* Program terminates normally inside setup */
   {
      background = 0;
      printf(" COMMAND-> ");
      fflush(stdout);
      length = read(STDIN_FILENO, inputBuffer, MAX_LINE); /* get next command */
      if (!evaluate(inputBuffer, length)) //see if 'r' cmd was used, add to history
         continue; //if error occured with 'r' re-ask for input
      setup(inputBuffer, args, &background, length); //setup for the cmd execution
      if (!executeCmd(args, background)) //execute the command
         return 1; //if an error happened with fork, exit program
   }
}


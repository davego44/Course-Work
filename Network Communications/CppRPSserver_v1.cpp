/*****************************************************************
* Program:
*    Lab RockSrv, Rock/Paper/Scissors with Sockets - Server Code
*    Brother Jones, CS 460
* Author:
*    David Lobaccaro
* Summary:
*    This program handles the server side of the RPS program.
*    It is responsible to gather the picked option from the
*    client and compute who is the winner or figure out if they
*    want to quit.
*****************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

//shows the errors
void error(const char *msg)
{
   perror(msg);
   exit(1);
}

//main computation
int main(int argc, char *argv[])
{
   int welcomeSocket, player1, player2, port;
   socklen_t clilen1, clilen2;
   char buffer[256];
   char buffer2[256];
   struct sockaddr_in serv_addr, cli_addr1, cli_addr2;
   int n;
   
   //check the arguments
   if (argc < 2) {
      fprintf(stderr,"ERROR, no port provided\n");
      exit(1);
   }
   //setup TCP
   welcomeSocket = socket(AF_INET, SOCK_STREAM, 0);
   if (welcomeSocket < 0)
      error("ERROR opening socket");
   bzero((char *) &serv_addr, sizeof(serv_addr));
   port = atoi(argv[1]);
   serv_addr.sin_family = AF_INET;
   serv_addr.sin_addr.s_addr = INADDR_ANY;
   serv_addr.sin_port = htons(port);
   if (bind(welcomeSocket, (struct sockaddr *) &serv_addr,
            sizeof(serv_addr)) < 0)
      error("ERROR on binding");
   listen(welcomeSocket,5);
   clilen1 = sizeof(cli_addr1);

   //connect player 1
   player1 = accept(welcomeSocket,
                    (struct sockaddr *) &cli_addr1,
                    &clilen1);
   if (player1 < 0)
      error("ERROR on accept");
   
   //connect player 2
   clilen2 = sizeof(cli_addr2);
   player2 = accept(welcomeSocket,
                    (struct sockaddr *) &cli_addr2,
                    &clilen2);
   if (player2 < 0)
      error("ERROR on accept");

   //read the picked option from both players
   bzero(buffer, 256);
   n = read(player1, buffer, 255);
   if (n < 0) error("ERROR reading from socket");

   bzero(buffer2, 256);
   n = read(player2, buffer2, 255);
   if (n < 0) error("ERROR reading from socket");

   while( (strcmp(buffer, "q\n") != 0) || (strcmp(buffer2, "q\n") != 0) )
   {
      //send the other players option to each player
      n = write(player1, buffer2, strlen(buffer2));
      if (n < 0) error("ERROR writing to socket");
      n = write(player2, buffer, strlen(buffer));
      if (n < 0) error("ERROR writing to socket");

      //check to see who won
      if ( (buffer == "r" && buffer2 == "s") ||
           (buffer == "s" && buffer2 == "p") ||
           (buffer == "p" && buffer2 == "r") )
      {
         //send the winner or loser message
         n = write(player1, "You Win!", 8);
         if (n < 0) error("ERROR writing to socket");
         n = write(player2, "You Lose!", 9);
         if (n < 0) error("ERROR writing to socket");
      } else
      {
         //send the winner or loser message
         n = write(player1, "You Lose!", 9);
         if (n < 0) error("ERROR writing to socket");
         n = write(player2, "You Win!", 8);
         if (n < 0) error("ERROR writing to socket");
      }

      //read the next option from the players
      bzero(buffer, 256);
      n = read(player1, buffer, 255);
      if (n < 0) error("ERROR reading from socket");

      bzero(buffer2, 256);
      n = read(player2, buffer2, 255);
      if (n < 0) error("ERROR reading from socket");
   }

   //if they picked to quit, quit
   close(player1);
   close(player2);
   close(welcomeSocket);
   return 0;
}

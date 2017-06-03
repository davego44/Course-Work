/**********************************************************************
* Program:
*    Lab RockClient, Rock/Paper/Scissors with Sockets - Client Code
*    Brother Jones, CS 460
* Author:
*    David Lobaccaro
* Summary:
*    This is the client of the RPS program. It gathers the player's
*    input (r, p, s, or q), sends it to the server, and receives
*    the winner. The player then can play again.
***********************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

//shows an error
void error(const char *msg)
{
   perror(msg);
   exit(0);
}

//has the main computation
int main(int argc, char *argv[])
{
   int mySocket, port, n;
   struct sockaddr_in serv_addr;
   struct hostent *server;

   //checks the arguments
   char buffer[256];
   if (argc < 3) {
      fprintf(stderr,"usage %s hostname port\n", argv[0]);
      exit(0);
   }
   //TCP setup
   port = atoi(argv[2]);
   mySocket = socket(AF_INET, SOCK_STREAM, 0);
   if (mySocket < 0)
      error("ERROR opening socket");
   server = gethostbyname(argv[1]);
   if (server == NULL) {
      fprintf(stderr,"ERROR, no such host\n");
      exit(0);
   }
   bzero((char *) &serv_addr, sizeof(serv_addr));
   serv_addr.sin_family = AF_INET;
   bcopy((char *)server->h_addr,
         (char *)&serv_addr.sin_addr.s_addr,
         server->h_length);
   serv_addr.sin_port = htons(port);
   if (connect(mySocket,(struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0)
      error("ERROR connecting");

   //display welcome message, get input, and check it
   printf("\nWelcome to the RPS program!\nRules are simple: rock beats scissors, scissors beat paper, and paper beats rock!\n");
   printf("Enter (r)ock, (p)aper, (s)cissors, or (q)uit: ");
   bzero(buffer, 256);
   fgets(buffer, 255, stdin);
   
   while(strcmp(buffer, "r\n") != 0 &&
         strcmp(buffer, "p\n") != 0 &&
         strcmp(buffer, "s\n") != 0 &&
         strcmp(buffer, "q\n") != 0)
   {
      printf("Invalid input. Enter r, p, s, or q: ");
      bzero(buffer, 256);
      fgets(buffer, 255, stdin);
      printf("%s\n",buffer);
   }
   //send the picked choice to the server
   n = write(mySocket, buffer, strlen(buffer));
   if (n < 0) error("ERROR writing to socket");

   while(strcmp(buffer, "q\n") != 0)
   {
      //read the other player's choice
      bzero(buffer, 256);
      n = read(mySocket, buffer, 255);
      if (n < 0) error("ERROR reading from socket");
      printf("Other player's choice: %s\n", buffer);

      //read the winning or losing message
      bzero(buffer, 256);
      n = read(mySocket, buffer, 255);
      if (n < 0) error("ERROR reading from socket");
      printf("%s\n", buffer);

      //more options
      printf("Play again! Enter (r)ock, (p)aper, (s)cissors, or (q)uit: ");
      bzero(buffer, 256);
      fgets(buffer, 255, stdin);
      while(strcmp(buffer, "r\n") != 0 &&
            strcmp(buffer, "p\n") != 0 &&
            strcmp(buffer, "s\n") != 0 &&
            strcmp(buffer, "q\n") != 0)
      {
         printf("Invalid input. Enter r, p, s, or q: ");
         bzero(buffer, 256);
         fgets(buffer, 255, stdin);
      }

      //send that option to the server
      n = write(mySocket, buffer, strlen(buffer));
      if (n < 0) error("ERROR writing to socket");
   }

   //if the player picked to quit, quit
   printf("\nThank you for playing!\n");
   close(mySocket);
   return 0;
}

/**
 * philospher4.c
 **/
int main()
{
   int semValue4;
   int semValue5;
   int roomValue;
   int semResult;
   int wait;
   int counter;

   semValue4 = semopen("sem4");
   semValue5 = semopen("sem5");
   roomValue = semopen("room");

   while( ( 1== 1 ) )
   {
     printf("4 is thinking.\n");
     
     semResult = semwait(roomValue);
     
     wait = rand(2);
     wait = (wait + 1);    
     for (counter = 0; (counter < wait); counter = (counter + 1))
     {
       /** Do nothing **/
     }     

     semResult = semwait(semValue4);
     for (counter = 0; (counter < 1); counter = (counter + 1))
     {
       /** Do nothing **/
     }     
     semResult = semwait(semValue5);
     
     printf("4 is eating.\n");
     wait = rand(1);
     wait = (wait + 1);
     
     for (counter = 0; (counter < wait); counter = (counter + 1))
     {
       /** Do nothing **/
     }     

     semResult = semsignal(semValue5);
     semResult = semsignal(semValue4);
     semResult = semsignal(roomValue);
   }

   /*semResult = semclose(semValue);*/
}

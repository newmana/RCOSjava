/**
 * philospher3.c
 **/
int main()
{
   int semValue3;
   int semValue4;
   int semResult;
   int wait;
   int counter;

   semValue3 = semopen("sem3");
   semValue4 = semopen("sem4");

   while( ( 1== 1 ) )
   {
     printf("3 is thinking.\n");
     
     wait = rand(2);
     wait = (wait + 1);    
     for (counter = 0; (counter < wait); counter = (counter + 1))
     {
       /** Do nothing **/
     }     

     semResult = semwait(semValue3);
     for (counter = 0; (counter < 1); counter = (counter + 1))
     {
       /** Do nothing **/
     }     
     semResult = semwait(semValue4);
     
     printf("3 is eating.\n");
     wait = rand(1);
     wait = (wait + 1);
     
     for (counter = 0; (counter < wait); counter = (counter + 1))
     {
       /** Do nothing **/
     }     

     semResult = semsignal(semValue3);
     semResult = semsignal(semValue4);
   }

   /*semResult = semclose(semValue);*/
}

/**
 * philospher5.c
 **/
int main()
{
   int semValue5;
   int semValue1;
   int semResult;
   int wait;
   int counter;

   semValue5 = semopen("sem5");
   semValue1 = semopen("sem1");

   while( ( 1== 1 ) )
   {
     printf("5 is thinking.\n");
     
     wait = rand(2);
     wait = (wait + 1);    
     for (counter = 0; (counter < wait); counter = (counter + 1))
     {
       /** Do nothing **/
     }     

     semResult = semwait(semValue5);
     for (counter = 0; (counter < 1); counter = (counter + 1))
     {
       /** Do nothing **/
     }     
     semResult = semwait(semValue1);
     
     printf("5 is eating.\n");
     wait = rand(1);
     wait = (wait + 1);
     
     for (counter = 0; (counter < wait); counter = (counter + 1))
     {
       /** Do nothing **/
     }     

     semResult = semsignal(semValue1);
     semResult = semsignal(semValue5);
   }

   /*semResult = semclose(semValue);*/
}

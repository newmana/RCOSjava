/**
 * philospher2.c
 **/
int main()
{
   int semValue2;
   int semValue3;
   int semResult;
   int wait;
   int counter;

   semValue2 = semopen("sem2");
   semValue3 = semopen("sem3");

   while( ( 1== 1 ) )
   {
     printf("2 is thinking.\n");
     
     wait = rand(2);
     wait = (wait + 1);    
     for (counter = 0; (counter < wait); counter = (counter + 1))
     {
       /** Do nothing **/
     }     

     semResult = semwait(semValue2);
     for (counter = 0; (counter < 1); counter = (counter + 1))
     {
       /** Do nothing **/
     }     
     semResult = semwait(semValue3);
     
     printf("2 is eating.\n");
     wait = rand(1);
     wait = (wait + 1);
     
     for (counter = 0; (counter < wait); counter = (counter + 1))
     {
       /** Do nothing **/
     }     

     semResult = semsignal(semValue3);
     semResult = semsignal(semValue2);
   }

   /*semResult = semclose(semValue);*/
}

/**
 * philospher1.c
 **/
int main()
{
   int semValue1;
   int semValue2;
   int semValue3;
   int semValue4;
   int semValue5;
   int semResult;
   int wait;
   int counter;

   semValue1 = semcreate("sem1",1);
   semValue2 = semcreate("sem2",1);
   semValue3 = semcreate("sem3",1);
   semValue4 = semcreate("sem4",1);
   semValue5 = semcreate("sem5",1);

   while( ( 1 == 1 ) )
   {
     printf("1 is thinking.\n");
     
     wait = rand(2);
     wait = (wait + 1);    
     for (counter = 0; (counter < wait); counter = (counter + 1))
     {
       /** Do nothing **/
     }     

     semResult = semwait(semValue1);
     for (counter = 0; (counter < 1); counter = (counter + 1))
     {
       /** Do nothing **/
     }     
     semResult = semwait(semValue2);
     
     printf("1 is eating.\n");
     wait = rand(1);
     wait = (wait + 1);
     
     for (counter = 0; (counter < wait); counter = (counter + 1))
     {
       /** Do nothing **/
     }     

     semResult = semsignal(semValue2);
     semResult = semsignal(semValue1);
   }

   /*semResult = semclose(semValue);*/
}

%{
/* adapted from lexical analyzer for the BenAri Pascal compiler */

#include <ctype.h>
#include <stdio.h>
#include <sys/types.h>

#include "ytab.h"
#include "../include/globdata.h"
#include "../include/compdata.h"
#include "../include/incfiles.h"
#include "../include/computil.h"

extern   int printf(const char*,...);
extern   int fprintf(FILE*,const char*,...);
extern   size_t fwrite(const void*,size_t,size_t,FILE*);
extern   int fgetc(FILE*);
extern   int fputc(int,FILE*);
extern   void yyerror(char*);

/* FLEX doesn't declare malloc and realloc before it uses them  */
/* This causes major problems in the MS-DOS version             */            
/* The stdlib.h file declares malloc and realloc in Borland C   */
#ifdef __TURBOC__
#include <stdlib.h>
#endif

BUFFER lbuf;
char	   numbertext[256];
int      numberbase;
char laststring[256];
char lasttoken[256];

void get_string(char );
void store_ident(char *);
void comment(void);

extern int yydebug;
extern int in_include;  /* yyparse signals entry into #include with this */

/* do it the way flex wants it */
int  baci_yyinput(char* buf, int maxsize);
#undef YY_INPUT
#define YY_INPUT(buf,result,maxsize) (result = baci_yyinput(buf,maxsize))
extern FILE* yyin;
%}

%%
%array
\n             ;
[ \t\32]       ;
"//".*\n       ;    

<<EOF>>  {
            curr_infile = close_infile(curr_infile);
            if (curr_infile < 0)  /* EOF in the original file */
               yyterminate();
            else {
               fprintf(yyout,"Returning to file %s\n", 
                  infile[curr_infile].fname);
            }
         }

"<="                    { strcpy(lasttoken,yytext);return(LE); }
atomic                  { strcpy(lasttoken,yytext);return(ATOMIC); }
break                   { strcpy(lasttoken,yytext);return(BREAK); }
case                    { strcpy(lasttoken,yytext);return(CASE); }
char                    { strcpy(lasttoken,yytext);return(CHAR); }
cin                     { strcpy(lasttoken,yytext);return(CIN); }
cobegin                 { strcpy(lasttoken,yytext);return(CBEGIN); }
const                   { strcpy(lasttoken,yytext);return(CONST); }
continue                { strcpy(lasttoken,yytext);return(CONTINUE); }
cout                    { strcpy(lasttoken,yytext);return(COUT); }
default                 { strcpy(lasttoken,yytext);return(DEFAULT); }
do                      { strcpy(lasttoken,yytext);return(DO); }
else                    { strcpy(lasttoken,yytext);return(ELSE); }
endl                    { strcpy(lasttoken,yytext);return(ENDL); }
extern                  { strcpy(lasttoken,yytext);return(EXTERN); }
for                     { strcpy(lasttoken,yytext);return(FOR); }
if                      { strcpy(lasttoken,yytext);return(IF); }
include                 { strcpy(lasttoken,yytext);return(INCLUDE); }
init                    { strcpy(lasttoken,yytext);return(INIT); }
int                     { strcpy(lasttoken,yytext);return(INT); }
main                    { strcpy(lasttoken,yytext);return(MAIN); }
monitor                 { strcpy(lasttoken,yytext);return(MONITOR); }
return                  { strcpy(lasttoken,yytext);return(RETURN); }
sprintf                 { strcpy(lasttoken,yytext);return(SPRINTF); }
sscanf                  { strcpy(lasttoken,yytext);return(SSCANF); }
string                  { strcpy(lasttoken,yytext);return(STRING); }
stringConcat            { strcpy(lasttoken,yytext);return(STRINGCONCAT); }
stringCopy              { strcpy(lasttoken,yytext);return(STRINGCOPY); }
stringCompare           { strcpy(lasttoken,yytext);return(STRINGCOMPARE); }
stringLength            { strcpy(lasttoken,yytext);return(STRINGLENGTH); }
switch                  { strcpy(lasttoken,yytext);return(SWITCH); }
typedef                 { strcpy(lasttoken,yytext);return(TYPEDEF); }
void                    { strcpy(lasttoken,yytext);return(VOID); }
while                   { strcpy(lasttoken,yytext);return(WHILE); }
send                    { strcpy(lasttoken,yytext);return(SEND); }
receive                 { strcpy(lasttoken,yytext);return(RECEIVE); }
broadcast               { strcpy(lasttoken,yytext);return(BROADCAST); }

[a-zA-Z_][a-zA-Z0-9_]* { 
      store_ident(yytext);
      strcpy(lasttoken,yytext);
      return(IDENTIFIER); 
   }


">="           { strcpy(lasttoken,yytext);return(GE); }
"!="           { strcpy(lasttoken,yytext);return(NE); }
"=="           { strcpy(lasttoken,yytext);return(EQ); }
"&&"           { strcpy(lasttoken,yytext);return(AND); }
"||"           { strcpy(lasttoken,yytext);return(OR); }
"++"           { strcpy(lasttoken,yytext);return(PLPL); }
"--"           { strcpy(lasttoken,yytext);return(MIMI); }
"<<"           { strcpy(lasttoken,yytext);return(LTLT); }
">>"           { strcpy(lasttoken,yytext);return(GTGT); }

";"   |
"."   |
"+"   |
"-"   |
"="   |
"*"   |
"%"   |
"/"   |
">"   |
"("   |
")"   |
"["   |
"]"   |
"{"   |
"}"   |
"&"   |
"|"   |
"!"   |
","   |
"#"   |
":"         { strcpy(lasttoken,yytext);return(yytext[0]); }

"<"      {  if (in_include) {
               get_string('>');
               strcpy(lasttoken,laststring);
               return FNSTRING;
            }
            else {
               strcpy(lasttoken,yytext);
               return (yytext[0]);
            }
         }

"\""     {  
            get_string('"');
            strcpy(lasttoken,laststring);
            return (in_include ? FNSTRING : RAWSTRING);
         }

[1-9][0-9]* { 
               strcpy(numbertext,yytext); 
               strcpy(lasttoken,yytext);
               numberbase = 10;
               return(UNSIGNED_INT);   
            }

0[0-9]*     { 
               strcpy(numbertext,yytext); 
               strcpy(lasttoken,yytext);
               numberbase = 8;
               return(UNSIGNED_INT);   
            }

0[xX][0-9a-fA-F]+ { 
                     strcpy(numbertext,&yytext[2]); /* 0x == yytext[0,1] */
                     strcpy(lasttoken,yytext);
                     numberbase = 16;
                     return(UNSIGNED_INT);   
                  }

"/*"     {  comment(); }

"'"      {
            get_string('\'');
            strcpy(lasttoken,laststring);
            if (strlen(laststring) > 1)
               yyerror("invalid character constant");
            return CHARCON;
         } 

.        { 
            sprintf(lbuf,"Illegal character '\\%x'", (int)yytext[0]);
            yyerror(lbuf);
         }

%%
/* current source line, ll = length, cc = curr. column */
int ll=0,cc=0;

/* the buffer holding the current line */
char  line[LINESIZE];

/*
   This routine store the current line from the current source file
   into a FLEX buffer when the lexer calls for input.

   Opening the original source file occurs in global_init, opening
   subsequent include files occurs in bac.y, the grammar, and closing
   the files occurs in the FLEX <<EOF>> specification rule above.
*/

int  baci_yyinput(char* buf, int maxsize)
{
int i;
char c;
char *p;
   cc = 0;
   ll = 0;
   p = line;
   do {  /* while null line */
      do { /* until new line or line buffer full */
         c = fgetc(yyin);
         if ((cc < LINESIZE)&&(c != (char)EOF)){
            *p++ = c;
            *buf++ = c;
            cc++;
         }
         else if (cc >= LINESIZE) {   /* cc >= LINESIZE */
            sprintf(lbuf,"Input line exceeds %d characters",LINESIZE);
            yyerror(lbuf);
            line[MAXLINE - 1] = '\n';    /* end the line with newline */
            buf[MAXLINE -1 ] = '\n';
         } 
      } while ((cc < LINESIZE) && (c != '\n')&&(c != (char)EOF));
      if ((cc == 0)&& (c == (char)EOF)) return YY_NULL;
      infile[curr_infile].line_no++;
      if (cc == LINESIZE)  /* well, almost as usual */
         cc = MAXLINE;
      buf[cc] = line[cc] = '\0';     /* terminate the line string */
      ll = cc;  /* ll is the length of the line */
      for (i = 0; i < include_level; i++) fputc('>',yyout);
      fprintf(yyout,"%4d %4d  %s",infile[curr_infile].line_no,lc,line);
      if (yydebug)
         printf("%4d %4d  %s\n",infile[curr_infile].line_no,lc,line);
   } while ((cc == 0) && (c == '\n')&&(c != (char)EOF));
   return ll;
}


void comment()
{
char c, lastc = ' ';
   while((c =input()))
   	if((lastc=='*') && (c =='/'))
   	   break;
      else if (c == (char)EOF) {
         yyerror("open comment at end-of-file");
         break;
      }
      else
	      lastc = c;
}

char get_escape()
   /* read escape char from input */
{
   unsigned char esc = 0;
   char c;
   int reading;
   int digit;

   c = input();
   if ((c == 'x')||(c == 'X')) {
      reading = 1;
      do {
         c = input();
         digit = (c - '0');
         if (digit <= 9 )
            esc = 16*esc + digit;
         else {
            if ((c >= 'a')&& (c <= 'f')) {
               digit = 10 + c - 'a';
               esc = 16*esc + digit;
            } 
            else if ((c >= 'A')&& (c <= 'F')) {
               digit = 10 + c - 'A';
               esc = 16*esc + digit;
            } 
            else {
               reading = 0;
               unput(c);
            }
         }
      } while (reading);
   }
   else {   /* maybe an octal? */
      reading = 1;
      do {
         digit = (c - '0');
         if ((digit >= 0)&&(digit <= 7)) {
            esc = 8*esc + digit;
            c = input();
         }
         else {
            reading = 0;
            unput(c);
         }
      } while (reading);
   }
   return ((esc < 256) ? esc : 0x20);
}


void get_string(char termch)
{
char c,*p=laststring;
   while((c = input())) {
      if (c == termch) { *p++ = '\0'; return; }
      switch (c){
         case '\\':  c = input();
                     switch (c) {
                         case 'b':  *p++ = '\b'; break;
                         case 'f':  *p++ = '\f'; break;
                         case 'n':  *p++ = '\n'; break;
                         case 'r':  *p++ = '\r'; break;
                         case 't':  *p++ = '\t'; break;
                         case 'v':  *p++ = '\v'; break;
                         case '0':  *p++ = get_escape(); break;
                         default:  *p++ = c;
                     }
                     break;
         case '\n':
         case '\014':
         case '\015':   yyerror("Non-terminated string"); return;
         default:       *p++ = c; break;
      } /* switch */
   }
}
   
/* Because two successive IDENTIFIER symbols can appear in the C */
/* grammar and yacc uses one-symbol lookahead, it's necessary to */
/* use a FIFO queue for the last identifier that yacc has seen   */
/*   However, during error recovery, identifiers accumulate in   */
/* identbuf[] up to the number of allowable errors before        */
/* bailout.                                                      */
#define MAXIDENT 20
char     identbuf[MAXIDENT][128];
int      ident_cnt = 0;
int      in_ident = 0;
int      out_ident = 0;

char *lastident()
{
char *p;
   if  (ident_cnt == 0)
      cfatal("No identifier available");
   p = &identbuf[out_ident][0];
   out_ident++;
   if (out_ident == MAXIDENT) out_ident = 0;
   ident_cnt--;
   return p;
}

void store_ident(char *s)
{
   if (ident_cnt == MAXIDENT)
      cfatal("Too many errors!!!");
   strcpy(&identbuf[in_ident][0],s);
   in_ident++;
   if (in_ident == MAXIDENT) in_ident = 0;
   ident_cnt++;
}

/*
 *
 *  $Log$
 *  Revision 2.9  1997/11/26 20:59:22  bynum
 *  add code to comment() to check for EOF
 *
 *  Revision 2.8  1997/11/20 01:56:01  bynum
 *  add (char)EOF typecasts for AIX
 *
 *  Revision 2.7  1997/11/19 14:16:33  bynum
 *  fix baci_yyinput() so that it accepts an emacs-produced file that has
 *  an EOF with no preceding newline
 *
 * Revision 2.6  1997/11/03  19:40:13  bynum
 * make declaration of fwrite acceptable to AIX, add sys/types.h include
 *
 * Revision 2.5  1997/10/27  06:55:51  bynum
 * change malloc, realloc declaraion to #ifdef __TURBOC__ include stdlib.h
 *
 * Revision 2.4  1997/10/26  20:05:53  bynum
 * add extern declarations for malloc and realloc
 *
 * Revision 2.3  1997/10/21  06:32:25  bynum
 * expand numbertext array to 256
 *
 * Revision 2.2  1997/09/04  10:52:25  bynum
 * add sscanf, sprintf
 *
 * Revision 2.1  1997/07/10  17:26:32  bynum
 * switch from fatal() to cfatal()
 *
 * Revision 2.0  1997/07/02  13:05:09  bynum
 * add extern
 *
 * Revision 1.15  1997/06/20  10:01:57  bynum
 * add send, receive, broadcast
 *
 * Revision 1.14  1997/06/17  05:26:48  bynum
 * add escaped characters to raw strings
 *
 * Revision 1.13  1997/06/02  06:56:30  bynum
 * add string type
 *
 * Revision 1.12  1997/05/13  10:16:16  bynum
 * enlarge MAXIDENT constant
 *
 * Revision 1.11  1997/04/01  12:41:13  bynum
 * add 'lasttoken', underscores
 *
 * Revision 1.10  1997/03/31  15:10:51  bynum
 * add tolerance to underscores in identifiers
 *
 * Revision 1.9  1997/03/26  20:41:35  bynum
 * remove isatty prototype
 *
 * Revision 1.8  1997/03/25  14:47:32  bynum
 * incorporate name changes in include directory, add prototypes to
 * silence complaints from gcc -Wall
 *
 * Revision 1.7  1997/02/04  07:00:18  bynum
 * switch to FLEX
 *
 * Revision 1.6  1996/03/06  13:54:37  bynum
 * add #ifdef MSDOS for ytab.h include
 *
 * Revision 1.5  1995/09/07  14:25:49  bynum
 * change to use new ../include directory structure, libba.a library
 *
 * Revision 1.4  1995/09/06  13:56:16  bynum
 * add 'const'
 *
 * Revision 1.3  1995/09/05  14:29:06  bynum
 * add CIN DEFAULT RETURN GTGT tokens
 *
 * Revision 1.2  1995/08/26  11:04:13  bynum
 * add octal, binary, hex number input, C++ output stream tokens, ++, --,
 * add 'lastident' FIFO string buffer
 *
 * Revision 1.1  1995/08/14  15:40:48  bynum
 * Initial revision
 *
 *
 */


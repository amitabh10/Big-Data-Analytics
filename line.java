import java.io.*;
class symbol	//for symbol table
{
	String var;		
	int len;
	int val;
	int addr;
}
class mot	//for mnemonic opcode table
{
	String mnem[]=new String[15];
	int opcode[]=new int[15];
}
class rot	//for register opcode table
{
	String reg[]=new String[4];
	int op[]=new int[4];
}
class lt	//for literal table
{
	int ltno;
	String ltval;
	int address;
}
class frt	//for forward reference table
{
	String symlt;
	int def;
	int usage[]=new int[10];
	int ucnt=0;
}
class pt	//for pool table
{
	int pno;
	int lindex;
}
public class line {
	static int lc=0,lccnt=0,symcnt=0,mocnt=0,rocnt=0,lcnt=0,fcnt=0,arcnt=0,pcnt=0;
	static int lcs[]=new int[50];
	static int mo[]=new int[50];
	static int ro[]=new int[50];
	static int ar[]=new int[50];
	static symbol sym[]=new symbol[10];
	static frt f[]=new frt[10];
	static lt l1[]=new lt[10];
	static pt p[]=new pt[10];
	static mot m;
	static rot r;
		public void pool()throws IOException
		{
			String strline = "";
	        BufferedReader br1 = new BufferedReader( new FileReader("F:/Work/TY/SP/input.txt"));	//reading file
	        while( (strline = br1.readLine()) != null)
	        {
	        	if(strline.equalsIgnoreCase("LTORG")||strline.equalsIgnoreCase("END"))	//finding LTORG or END
	        	{
	        		String next=br1.readLine();	//After LTORG or END,to get literals
	        		next=next.trim();	//removing white spaces before and after the given literal
	        		for(int i=0;i<lcnt;i++)
	        		{
	        			if(next.equalsIgnoreCase(l1[i].ltval))	//comparing with literals in the literal table
	        			{
	        				p[pcnt]=new pt();
	        				p[pcnt].pno=pcnt+1;
	        				p[pcnt].lindex=l1[i].ltno;	//referring the literal index from the literal table
	        				pcnt++;
	        			}
	        		}
	        	}
	        }
	        System.out.println("\n---------------Pool Table---------------");	//displaying the pool table
	        System.out.println("\nNO\tLITERAL_INDEX\n");
	        for(int i=0;i<pcnt;i++)
	        	System.out.println(p[i].pno+"\t#"+p[i].lindex);
	        br1.close();
		}
		public void createfrt(String str,int no)
		{
			String words[]=str.split(" ");	//splitting the string by spaces into an array
			for(int i=0;i<words.length;i++)
			{
				if(words[i].contains(","))	//since after comma there can be a symbol or literal
				{
					String w[]=words[i].split(",");	//splitting the word containing the comma 
					for(int j=0;j<symcnt;j++)
					{
						if(w[1].equalsIgnoreCase(sym[j].var))	//comparing with symbols
						{
							int x=fcnt,k;
							for(k=0;k<fcnt;k++)
							{
								if(f[k].symlt.equalsIgnoreCase(w[1]))	//for more than one address of usage
								{
									x=k;
									break;
								}
							
							}
							if(k==fcnt)	
							{
								f[fcnt]=new frt();
								x=fcnt;
								fcnt++;
							}
							f[x].symlt=w[1];
							f[x].def=sym[j].addr;
							ar[no]=sym[j].addr;
							f[x].usage[f[x].ucnt]=lcs[no];
							f[x].ucnt++;
						}
					}
					for(int j=0;j<lcnt;j++)	//similarly for literals
					{
						if(w[1].equalsIgnoreCase(l1[j].ltval))
						{
							int x=fcnt,k;
							for(k=0;k<fcnt;k++)
							{
								if(f[k].symlt.equalsIgnoreCase(w[1]))
								{
									x=k;
									break;
								}
							
							}
							if(k==fcnt)
							{
								f[fcnt]=new frt();
								x=fcnt;
								fcnt++;
							}
							f[x].symlt=w[1];
							f[x].def=l1[j].address;
							ar[no]=l1[j].address;
							f[x].usage[f[x].ucnt]=lcs[no];
							f[x].ucnt++;
						}
					}
				}
				else
				{
					for(int k=0;k<symcnt;k++)
					{
						if(words[i].equalsIgnoreCase(sym[k].var) && i!=0)	//for avoiding symbols coming before DS or DC
						{
							int x=fcnt,j;
							for(j=0;j<fcnt;j++)
							{
								if(f[k].symlt.equalsIgnoreCase(words[i]))
								{
									x=j;
									break;
								}
							
							}
							if(j==fcnt)
							{
								f[fcnt]=new frt();
								x=fcnt;
								fcnt++;
							}
							f[x].symlt=words[i];
							f[x].def=sym[k].addr;
							ar[no]=sym[k].addr;	//placing address of reference in machine code
							f[x].usage[f[x].ucnt]=lcs[no];
							f[x].ucnt++;
						}
					}
				}
			}
			arcnt++;
		}
		public void literal(String str,int lc)
		{
			if(str.startsWith(" "))	//literals can be found for lines starting with space
			{
				l1[lcnt]=new lt();
				l1[lcnt].ltno=lcnt+1;
				l1[lcnt].ltval=str.substring(1);	//literal value is present after whitespace
				l1[lcnt].address=lc;
				lcnt++;
			}
			
		}
		public void placero(String str)	//placing register opcodes in machine code from ROT
		{
			String c[]=str.split(" ");	//splitting string into words of an array by white spaces
			for(int i=0;i<c.length;i++)
			{
				if(c[i].contains(","))	//taking words containing comma
				{
					String d=c[i].substring(0,c[i].indexOf(","));	//register name is present before comma
					for(int j=0;j<4;j++)
					{
						if(d.equalsIgnoreCase(r.reg[j]))	//comparing with register name in ROT
						{
							ro[rocnt]=r.op[j];	//placing opcodes in array
						}
					}
				}
			}
			rocnt++;
		}
		public void newrot()	//creating hard-coded ROT
		{
			r=new rot();
			r.reg[0]="A";
			r.op[0]=01;
			r.reg[1]="B";
			r.op[1]=02;
			r.reg[2]="C";
			r.op[2]=03;
			r.reg[3]="D";
			r.op[3]=04;
		}
		public void placemo(String str)	//placing mnemonic opcode values from MOT 
		{
			String b[]=str.split(" ");	//splitting string by spaces
			for(int i=0;i<b.length;i++)
			{
				if(b[i].contains(":"))	//since mnemonic can be there after colon
				{
					b[i]=b[i].substring(str.indexOf(":")+1,str.indexOf(" "));	//taking word only after colon
				}
				for(int j=0;j<8;j++)
				{
					if(b[i].equalsIgnoreCase(m.mnem[j]))	//comparing words with mnemonic in MOT
					{
						mo[mocnt]=m.opcode[j];	//placing opcode values in array
					}
				}
			}
			mocnt++;
		}
		public void newmot()	//creating hard-coded mnemonic opcode table
		{
			m =new mot();
			m.mnem[0]="START";
			m.opcode[0]=01;
			m.mnem[1]="MOVER";
			m.opcode[1]=02;
			m.mnem[2]="ADD";
			m.opcode[2]=03;
			m.mnem[3]="MOVEM";
			m.opcode[3]=04;
			m.mnem[4]="SUB";
			m.opcode[4]=05;
			m.mnem[5]="MULT";
			m.opcode[5]=06;
			m.mnem[6]="STOP";
			m.opcode[6]=07;
			m.mnem[7]="BC";
			m.opcode[7]=Integer.parseInt("08");
		}
		public void loccnt(String words[])	//finding LC in machine code
		{
			
			for(int i=0;i<words.length;i++)
            {	//checking if word is START,LTORG,ORIGIN or END for which LC is 0
				if(words[i].equalsIgnoreCase("START")||words[i].equalsIgnoreCase("LTORG")||words[i].equalsIgnoreCase("ORIGIN")||words[i].equalsIgnoreCase("END"))
				{
					if(words[i].equalsIgnoreCase("START"))	//for taking the starting location value
					{	
						lc=Integer.parseInt(words[i+1]);
						lcs[lccnt]=0;
						lccnt++;
						break;
					}
					else
					{
						lcs[lccnt]=0;
						lccnt++;
						break;
					}
				}
				else
				{
					if(i==0)	//defining LC for every line once
					{
						lcs[lccnt]=lc;
						lc++;
						lccnt++;
					}
				}
            }
			
		}
		public void sym(String str,int l)	//creating symbol table
		{
		
			if(str.contains(":"))	//for symbols such as NEXT
			{
				sym[symcnt]=new symbol();
				sym[symcnt].var=str.substring(0, str.indexOf(":"));	//storing symbol name in ST
				sym[symcnt].val=0;
				sym[symcnt].len=0;
				sym[symcnt].addr=l;
				symcnt++;
			}
			else
			{
				String a[]=str.split(" ");	//symbols can be there before DC
				for(int i=0;i<a.length;i++)
				{
					if(a[i].equalsIgnoreCase("DC"))
					{
						sym[symcnt]=new symbol();
						sym[symcnt].var=a[i-1];	
						String val=a[i+1];	//value is present for symbol in line having DC
						String val1[]=val.split("'");	//for separating value from quotes
						sym[symcnt].val=Integer.parseInt(val1[1]);
						sym[symcnt].len=0;
						sym[symcnt].addr=l;
						symcnt++;
						break;
					}
					if(a[i].equalsIgnoreCase("DS"))	//symbols can be there before DS
					{
						sym[symcnt]=new symbol();
						sym[symcnt].var=a[i-1];
						String len=a[i+1];	//taking length for symbol in line having DS
						sym[symcnt].len=Integer.parseInt(len);
						sym[symcnt].val=0;
						sym[symcnt].addr=l;
						symcnt++;
						break;
					}
					
				}
			}
			
		}
		public static void main(String a[])throws IOException{
	        String strLine = "";
	        line l=new line();
	        l.newmot();
	        l.newrot();
	        System.out.println("The Input File:");	
	        BufferedReader br = new BufferedReader( new FileReader("F:/Work/TY/SP/input.txt"));	//reading file line by line
	        while( (strLine = br.readLine()) != null){
	             System.out.println(strLine);
	             String words[]=strLine.split(" ");	//splitting string into array of words
	             l.loccnt(words);	//calling function for finding LC
	             l.sym(strLine,lcs[lccnt-1]);	//calling function for creating symbol table
	             l.placemo(strLine);	//calling function for creating mnemonic opcode values
	             l.placero(strLine);	//calling function for placing register opcode values
	             l.literal(strLine,lcs[lccnt-1]);	//calling function for creating literal table
	        }
	        br.close();	//closing file
	        br=new BufferedReader(new FileReader("F:/Work/TY/SP/input.txt"));	//reading file 
	        int k=0;
	        while( (strLine = br.readLine()) != null){
	        	l.createfrt(strLine,k);	//calling function for creating FRT
	        	k++;
	        }
	        System.out.println("\n-----------------Symbol Table--------------");
	        System.out.println("\nSYMBOL\tLENGTH\tVALUE\tADDRESS\n"); //displaying symbol table
	        for(int i=0;i<symcnt;i++)
	        	System.out.println(sym[i].var+"\t"+sym[i].len+"\t"+sym[i].val+"\t"+sym[i].addr);
	        System.out.println("\n-----------------Mnemonic Opcode Table--------------");	//displaying MOT
	        System.out.println("\nMNEMONIC\tOPCODE\n");
	        for(int i=0;i<8;i++)
	        	System.out.println(m.mnem[i]+"\t\t"+m.opcode[i]);
	        System.out.println("\n-----------------Register Opcode Table--------------");	//displaying ROT
	        System.out.println("\nREGISTER\tOPCODE\n");
	        for(int i=0;i<4;i++)
	        	System.out.println(r.reg[i]+"\t\t"+r.op[i]);
	        
	        System.out.println("\n-----------------Literal Table--------------");	//displaying LT
	        System.out.println("\nINDEX\tLITERAL\tADDRESS\n");
	        for(int i=0;i<lcnt;i++)
	        	System.out.println(l1[i].ltno+"\t"+l1[i].ltval+"\t"+l1[i].address);
	        System.out.println("\n--------------------FRT Table--------------------");
	        System.out.println("\nSYM/LIT\t\tADDR_DEF\tADDR_USAGE\n");	//displaying FRT
	        for(int i=0;i<fcnt;i++)
	        {
	        	System.out.print(f[i].symlt+"\t\t"+f[i].def+"\t\t");
	        	for(int j=0;j<f[i].ucnt;j++)
	        	{
	        		System.out.print(f[i].usage[j]+"  ");
	        	}
	        	System.out.println("\n");
	        }
	        System.out.println("------------------Machine Code-------------------");
	        System.out.println("\nLC\tMO\tRO\tAR\n");	//displaying machine code
	        for(int i=0;i<arcnt;i++)
	        	System.out.println(lcs[i]+"\t"+mo[i]+"\t"+ro[i]+"\t"+ar[i]); //placing ro values
	        br.close();
	        l.pool();	//calling function for creating Pool table
	       }
}
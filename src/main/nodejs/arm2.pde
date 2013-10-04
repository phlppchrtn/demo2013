int[][] playground;
boolean playerA;

void setup() {
  size(400, 400);
  noLoop();
 
  playground = new int[3][3];
	for(int i=0; i<	3; i++){
		for(int j=0; j<	3; j++){
			playground[i][j]=0;
		}
	}
}

void mousePressed() {
	int i = floor (3 *mouseX/width);
	int j = floor (3* mouseY/height);
	println("press i=" + i + ", j="+j);
	press(i, j);
	select(i, j);
}

/*
void mouseOver() {
	press(mouseX, mouseY);
	select(mouseX, mouseY);
}
*/

void press(int i , int j){
	playerA = ! playerA;
	if(playerA){
		playground[i][j]=1;
	}else{
		playground[i][j]=2;
	}
	fill(255,0,0);
	//ellipse(x, y, 10, 10);	
	draw();
}

void draw() {
	background(255);
	fill(255);	
	rect(0,0, width-1, height-1);

	line (width/3, 0, width/3, height);
	line (2*width/3, 0, 2*width/3, height);
	line (3*width/3, 0, 3*width/3, height);

	line (0, height/3, width, height/3);
	line (0, 2*height/3, width, 2*height/3);
	line (0, 3*height/3, width, 3*height/3);

	for(int i=0; i<	playground.length; i++){
		for(int j=0; j<	playground.length; j++){
			int x = width/6 * (1+2*i);
			int y = height/6 * (1+2*j);
			
			if (playground[i][j]==1){
				fill(255,0,0);
				ellipse(x, y, 10, 10);	
			}
			if (playground[i][j]==2){
				fill(0, 255,0);
				ellipse(x, y, 10, 10);	
			}
		}
	}
}

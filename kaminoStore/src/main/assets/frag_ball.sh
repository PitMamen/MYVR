precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D uTexture;
uniform float uAlpha;//透明度
void main()
{
	vec4 bcolor = texture2D(uTexture, vTextureCoord);
	float temp = bcolor.a;
	bcolor.a = temp * uAlpha;
	gl_FragColor = bcolor;
}
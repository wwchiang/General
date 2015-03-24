sampler2D input : register(s0);
float frequency : register(C0);
//Game -- Going to add a point light (as a flashlight) for final version.
float4 PixelShaderFunction(float2 uv: TEXCOORD0) : COLOR0
{
	
	float4 color = tex2D(input, uv);
	color -= tex2D(input, uv - 0.002) * 2.5f;
	color += tex2D(input, uv + 0.002) * 2.5f;

    return color;
    
}
////Start Screen
float4 PixelShaderFunction2(float2 uv : TEXCOORD0) : COLOR0
{

	float distfromcenter=distance(float2(0.4f, 0.6f), uv );
	float4 rColor = lerp(float4 (0,0,0,1),float4(0.8,0,0,1), saturate(distfromcenter));
	rColor *= 0.55;
	return rColor;
	
}
 

#define WEIGHT_COUNT 6

float weight[WEIGHT_COUNT] = {
	0.9,
	0.85,
	0.70,
	0.50,
	0.25,
	0.10
};

float colorIntensity = 1.0f;
float intensity = 0.7f;
float2 pixelAspect = {1.0/1280, 1.0/768};

	float4 PS_BlurHorizontal(in float2 uv : TEXCOORD) : COLOR 
{ 
	float4 Color = tex2D(input, uv);
		float mult = 1;
	for(int i=0; i<WEIGHT_COUNT; i++)
	{
		Color += tex2D(input, float2(uv.x-(intensity*pixelAspect.x*mult), uv.y)) * weight[i];
		Color += tex2D(input, float2(uv.x+(intensity*pixelAspect.x*mult), uv.y)) * weight[i];
		mult = mult + 4;
	}
	Color /= WEIGHT_COUNT;
	return Color * colorIntensity; 
}

float4 PS_BlurVertical(in float2 uv : TEXCOORD) : COLOR
{ 
	float4 Color = tex2D(input, uv);
		float mult = 1;
	for(int i=0; i<WEIGHT_COUNT; i++)
	{
		Color += tex2D(input, float2(uv.x, uv.y-(intensity*pixelAspect.y*mult))) * weight[i];
		Color += tex2D(input, float2(uv.x, uv.y+(intensity*pixelAspect.y*mult))) * weight[i];
		mult = mult + 4;
	}
	Color /= WEIGHT_COUNT;
	return Color * colorIntensity;
}


#define SAMPLE_COUNT 15

float2 SampleOffsets[SAMPLE_COUNT];
float SampleWeights[SAMPLE_COUNT];
//Blurs using a 3x3 filter kernel
	float4 BlurFunction3x3(float2 uv : TEXCOORD0) : COLOR0
{
	//colorin is the original color of the pixel, colorout the altered color

		float4 colorin, colorout;
	colorin = tex2D( input , uv.xy);
	colorout = colorin;

	if((colorin.g + colorin.b) < colorin.r )
	{
		colorout.r = colorin.r;
		colorout.g = 0;
		colorout.b = 0;

	}else
	{
		colorout.rgb = dot(colorin.rgb, colorin.rgb ) * dot(colorin.rgb, float4(2, 1, 2, 1) );
	}

	return colorout;





}

technique FunkyBlur
{
	pass Pass1
	{
		// A post process shader only needs a pixel shader.
		PixelShader = compile ps_2_0 PS_BlurHorizontal();
	}
	pass Pass2
	{
		// A post process shader only needs a pixel shader.
		PixelShader = compile ps_2_0 PS_BlurVertical();
	}
	pass Pass3
	{
		PixelShader = compile ps_2_0 BlurFunction3x3();
	}
	pass Pass4
	{
		PixelShader = compile ps_2_0 PixelShaderFunction2();
	}
	pass Pass5
	{
		PixelShader = compile ps_2_0 PixelShaderFunction();
	}
	pass Pass6
	{

	}
}


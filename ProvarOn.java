import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProvarOn {

    public static void main(String[] args) {

        String polimonio;
        String notacao;
        String print = "Ops Deu erro entre em contato com o Desenvolvedor (99) 9 9856 - 1924";

        try(Scanner e = new Scanner(System.in)){
            System.out.println("Digite o Polimônio: Ex( n^(2) + 800 )");
            polimonio = e.nextLine();

            System.out.println("Digite a Notação Assintótica: Ex( O(n^(2)) )");
            notacao = e.nextLine();

//            polimonio = "4n^(2 + n) + 5";
//            notacao = "O(n^(2 + n))";

            print = polimonio + "Não é " + notacao;

            for (int i = 1; i < 10000; i++) {

                BigDecimal c = new BigDecimal(i);
                BigDecimal m = new BigDecimal(Math.round((i + 9) /10));

                BigDecimal poli = resolverOperacaoPolimonio(resoverString(polimonio, m));

                BigDecimal nota = resolverOperacaoPolimonio(resoverString((
                        c + notacao.substring(2,notacao.length()-1)
                ), m));


                if(poli.doubleValue() <= nota.doubleValue()){
                    print = "Correto, com c = " + c + " e m = " + m;
                    break;
                }

            }

        }catch (Exception e){
            print = "Ops Deu erro Tente novamente ou entre em contato com o Desenvolvedor (99) 9 9856 - 1924";
//            e.printStackTrace();
        }

        System.out.println(print);

    }

    private static String resoverString(String str, BigDecimal m){
        return str
                .replaceAll(" ", "")
                .replaceAll("(\\d)n", "$1*" + m)
                .replaceAll("n", String.valueOf(m));
    }

    private static BigDecimal resolverOperacaoPolimonio(String polimonio){

        if(polimonio.contains("^")){
            polimonio = resolverExpoente(polimonio);
        }

        Map<String, CalcularOperacao> operacaoMap = construirMap();
        String[] operacoes = new String[] {"[\\*Xx]","\\/", "\\-", "\\+"};

        return new BigDecimal(calcularTudo(polimonio, operacoes,3, operacaoMap));
    }

    private static String calcularTudo(String polimonio,
                                       String[] operacoes,
                                       int indexOperacao,
                                       Map<String, CalcularOperacao> operacaoMap){

        if(indexOperacao > 0){
            polimonio = calcularTudo(polimonio,operacoes, (indexOperacao-1), operacaoMap);
        }

        return resolverPolimonio(String.valueOf(polimonio),
                operacoes[indexOperacao],
                operacaoMap.get(operacoes[indexOperacao]));
    }

    private static String resolverPolimonio(String polimonio, String regex, CalcularOperacao calcularOperacao){

        Pattern pattern = Pattern.compile("([0-9|\\.]+)(" + regex + "[0-9|\\.]+)+");
        Matcher matcher = pattern.matcher(polimonio);
        String aux = polimonio;

        while(matcher.find()){
            String substring = polimonio.substring(matcher.start(), matcher.end());
            String resultado = resolverCalculo(substring,calcularOperacao,regex).toString();
            aux = aux.replace(substring, resultado);
            if(Pattern.compile(regex).matcher(resultado).find()){
                aux = resolverPolimonio(
                        new StringBuilder(aux).deleteCharAt(matcher.start()-1).toString()
                        ,regex,
                        calcularOperacao);
            }
        }

        return aux;
    }

    private static BigDecimal resolverCalculo(String polimonio, CalcularOperacao calcularOperacao, String regex){
        if(!Pattern.compile(regex).matcher(polimonio).find()){
            return new BigDecimal(polimonio.replaceAll(regex, ""));
        }

        return calcularOperacao.calular(new BigDecimal(polimonio.split(regex)[0].trim()),
                resolverCalculo(
                        polimonio.replaceFirst("[0-9|\\.]+"+regex, ""),
                        calcularOperacao,
                        regex));
    }

    private static Map<String, CalcularOperacao> construirMap(){
        Map<String, CalcularOperacao> operacaoMap = new HashMap<>();

        operacaoMap.put("\\+", BigDecimal::add);
        operacaoMap.put("\\-", BigDecimal::subtract);
        operacaoMap.put("\\/", (a,b) -> a.divide(b, 2, RoundingMode.HALF_UP));
        operacaoMap.put("[\\*Xx]", BigDecimal::multiply);

        return operacaoMap;
    }

    private static String resolverExpoente(String polimonio){
        String resultPolimonio = polimonio;
        Pattern regex = Pattern.compile("\\d(\\^\\().+(\\))");
        Matcher matcher = regex.matcher(polimonio);
        while (matcher.find()){
            BigDecimal aux = resolverOperacaoPolimonio(polimonio.substring(matcher.start()+3, matcher.end()-1));
            double pow = Math.pow(Double.parseDouble(String.valueOf(polimonio.charAt(matcher.start()))), aux.doubleValue());
            resultPolimonio = resultPolimonio.replace(polimonio.substring(matcher.start(), matcher.end()), Math.abs(pow) + "");
        }
        return resultPolimonio;
    }

    private static boolean resolverCompatibilidade(String polimonio, String notacao){
        return polimonio.contains(notacao);
    }

}
interface CalcularOperacao{
    BigDecimal calular(BigDecimal a, BigDecimal b);
}

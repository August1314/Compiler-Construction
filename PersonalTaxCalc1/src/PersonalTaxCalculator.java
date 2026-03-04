/**
 * 个人所得税计算器
 * 用于计算工资、薪金所得的个人所得税
 * 支持调整起征点和税率
 */
public class PersonalTaxCalculator {
    private static double taxThreshold = 5000; // 起征点
    private static TaxRate[] taxRates = {
        new TaxRate(0, 3000, 0.03),
        new TaxRate(3000, 12000, 0.10),
        new TaxRate(12000, 25000, 0.20),
        new TaxRate(25000, 35000, 0.25),
        new TaxRate(35000, 55000, 0.30),
        new TaxRate(55000, 80000, 0.35),
        new TaxRate(80000, Double.MAX_VALUE, 0.45)
    };
    
    /**
     * 主方法
     */
    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        boolean exit = false;
        
        while (!exit) {
            displayMenu();
            System.out.print("请选择功能：");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // 消耗换行符
                
                switch (choice) {
                    case 1:
                        calculateTax(scanner);
                        break;
                    case 2:
                        adjustThreshold(scanner);
                        break;
                    case 3:
                        adjustTaxRates(scanner);
                        break;
                    case 4:
                        exit = true;
                        System.out.println("感谢使用个人所得税计算器！");
                        break;
                    default:
                        System.out.println("无效选择，请重新输入！");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("输入错误，请输入数字！");
                scanner.nextLine(); // 清除错误输入
            }
        }
        
        scanner.close();
    }
    
    /**
     * 显示菜单
     */
    private static void displayMenu() {
        System.out.println("\n=== 个人所得税计算器 ===");
        System.out.println("1. 计算个人所得税");
        System.out.println("2. 修改起征点");
        System.out.println("3. 修改税率");
        System.out.println("4. 退出");
        System.out.println("当前起征点：" + taxThreshold + "元");
    }
    
    /**
     * 计算个人所得税
     */
    private static void calculateTax(java.util.Scanner scanner) {
        System.out.print("请输入当月工资薪金总额：");
        try {
            double income = scanner.nextDouble();
            scanner.nextLine(); // 消耗换行符
            
            double taxableIncome = income - taxThreshold;
            double tax = 0;
            
            if (taxableIncome <= 0) {
                System.out.println("您的收入未达到起征点，无需纳税！");
                return;
            }
            
            // 计算应纳税额
            for (TaxRate rate : taxRates) {
                if (taxableIncome > rate.getStart()) {
                    double amount = Math.min(taxableIncome, rate.getEnd()) - rate.getStart();
                    tax += amount * rate.getRate();
                }
            }
            
            System.out.printf("工资薪金总额：%.2f元\n", income);
            System.out.printf("应纳税所得额：%.2f元\n", taxableIncome);
            System.out.printf("应缴纳个人所得税：%.2f元\n", tax);
        } catch (java.util.InputMismatchException e) {
            System.out.println("输入错误，请输入数字！");
            scanner.nextLine(); // 清除错误输入
        }
    }
    
    /**
     * 调整起征点
     */
    private static void adjustThreshold(java.util.Scanner scanner) {
        System.out.print("请输入新的起征点（元）：");
        try {
            double newThreshold = scanner.nextDouble();
            scanner.nextLine(); // 消耗换行符
            
            if (newThreshold >= 0) {
                taxThreshold = newThreshold;
                System.out.println("起征点已修改为：" + taxThreshold + "元");
            } else {
                System.out.println("起征点不能为负数！");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("输入错误，请输入数字！");
            scanner.nextLine(); // 清除错误输入
        }
    }
    
    /**
     * 调整税率
     */
    private static void adjustTaxRates(java.util.Scanner scanner) {
        System.out.println("当前税率表：");
        for (int i = 0; i < taxRates.length; i++) {
            TaxRate rate = taxRates[i];
            System.out.printf("级数%d：%s - %s元，税率：%.2f%%\n", 
                i + 1, 
                rate.getStart() == 0 ? "0" : rate.getStart(),
                rate.getEnd() == Double.MAX_VALUE ? "∞" : rate.getEnd(),
                rate.getRate() * 100
            );
        }
        
        System.out.print("请输入要修改的级数（1-7）：");
        try {
            int level = scanner.nextInt();
            scanner.nextLine(); // 消耗换行符
            
            if (level >= 1 && level <= 7) {
                System.out.print("请输入新的税率（例如：0.03表示3%）：");
                double newRate = scanner.nextDouble();
                scanner.nextLine(); // 消耗换行符
                
                if (newRate >= 0 && newRate <= 1) {
                    taxRates[level - 1].setRate(newRate);
                    System.out.println("税率已修改！");
                } else {
                    System.out.println("税率必须在0-1之间！");
                }
            } else {
                System.out.println("级数必须在1-7之间！");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("输入错误，请输入数字！");
            scanner.nextLine(); // 清除错误输入
        }
    }
}

/**
 * 税率等级类
 * 用于存储每级税率的信息
 */
class TaxRate {
    private double start; // 起始金额
    private double end;   // 结束金额
    private double rate;  // 税率
    
    /**
     * 构造方法
     */
    public TaxRate(double start, double end, double rate) {
        this.start = start;
        this.end = end;
        this.rate = rate;
    }
    
    /**
     * 获取起始金额
     */
    public double getStart() {
        return start;
    }
    
    /**
     * 获取结束金额
     */
    public double getEnd() {
        return end;
    }
    
    /**
     * 获取税率
     */
    public double getRate() {
        return rate;
    }
    
    /**
     * 设置税率
     */
    public void setRate(double rate) {
        this.rate = rate;
    }
}

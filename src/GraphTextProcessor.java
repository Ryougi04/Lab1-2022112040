import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import java.io.*;
import java.util.*;

public class GraphTextProcessor {
    public static Map<String, Map<String, Integer>> graph = new HashMap<>();
    private static List<String> wordSequence = new ArrayList<>();
    private static Random rand = new Random();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        String filePath ="Cursed Be The Treasure.txt";
        parseTextFile(filePath);

        while (true) {
            System.out.println("\n请选择功能：");
            System.out.println("1. 展示有向图");
            System.out.println("2. 查询桥接词");
            System.out.println("3. 生成新文本");
            System.out.println("4. 最短路径");
            System.out.println("5. PageRank");
            System.out.println("6. 随机游走");
            System.out.println("0. 退出");
            System.out.print("输入选项编号: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showDirectedGraph(graph);
                    break;
                case "2":
                    System.out.print("输入两个单词，用空格分隔: ");
                    String[] input = scanner.nextLine().split("\\s+");
                    System.out.println(queryBridgeWords(input[0], input[1]));
                    break;
                case "3":
                    System.out.print("输入文本: ");
                    String inputText = scanner.nextLine();
                    System.out.println(generateNewText(inputText));
                    break;
                case "4":
                    System.out.print("输入起点和终点单词: ");
                    String[] pathInput = scanner.nextLine().split("\\s+");
                    System.out.println(calcShortestPath(pathInput[0], pathInput[1]));
                    break;
                case "5":
                    System.out.print("输入单词计算PageRank: ");
                    String word = scanner.nextLine();
                    System.out.println("PageRank \"" + word + "\" = " + String.format("%.4f", calPageRank(word)));                    break;
                case "6":
                    System.out.println(randomWalk());
                    break;
                case "0":
                    System.out.println("退出程序。");
                    return;
                default:
                    System.out.println("无效选项！");
            }
        }
    }

    public static void parseTextFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append(" ");  // 注意加空格，避免单词黏连
        }
        reader.close();

        // 统一替换和小写处理
        String content = sb.toString().replaceAll("[^A-Za-z]+", " ").toLowerCase().trim();
        String[] splitWords = content.split("\\s+");

        for (String word : splitWords) {
            if (!word.isEmpty()) {
                wordSequence.add(word);
            }
        }

        for (int i = 0; i < wordSequence.size() - 1; i++) {
            String from = wordSequence.get(i);
            String to = wordSequence.get(i + 1);
            graph.putIfAbsent(from, new HashMap<>());
            Map<String, Integer> neighbors = graph.get(from);
            neighbors.put(to, neighbors.getOrDefault(to, 0) + 1);
        }
    }

    public static void showDirectedGraph(Map<String, Map<String, Integer>> graphData) {
        System.setProperty("org.graphstream.ui", "swing");
        Graph g = new SingleGraph("Directed Graph");

        for (String node : graphData.keySet()) {
            if (g.getNode(node) == null) {
                g.addNode(node).setAttribute("ui.label", node);
            }
            for (Map.Entry<String, Integer> entry : graphData.get(node).entrySet()) {
                String neighbor = entry.getKey();
                int weight = entry.getValue();
                if (g.getNode(neighbor) == null) {
                    g.addNode(neighbor).setAttribute("ui.label", neighbor);
                }
                String edgeId = node + "-" + neighbor;
                if (g.getEdge(edgeId) == null) {
                    Edge e = g.addEdge(edgeId, node, neighbor, true);
                    e.setAttribute("ui.label", String.valueOf(weight));
                }
            }
        }

        g.setAttribute("ui.stylesheet",
            "node {" +
            " fill-color: lightblue;" +
            " size: 30px;" +
            " text-size: 24px;" +
            "}" +
            "edge {" +
            " arrow-shape: arrow;" +
            " fill-color: gray;" +
            " text-size: 14px;" +
            "}");

        g.display();
    }

    public static String queryBridgeWords(String word1, String word2) {
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No \"" + word1 + "\" or \"" + word2 + "\" in the graph!";
        }
        Set<String> bridgeWords = new HashSet<>();
        for (String intermediate : graph.get(word1).keySet()) {
            if (graph.containsKey(intermediate) && graph.get(intermediate).containsKey(word2)) {
                bridgeWords.add(intermediate);
            }
        }
        if (bridgeWords.isEmpty()) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        }
        List<String> bridgeList = new ArrayList<>(bridgeWords);
        if (bridgeList.size() == 1) {
            return "The bridge word from \"" + word1 + "\" to \"" + word2 + "\" is: " + bridgeList.get(0) + ".";
        } else {
            StringJoiner joiner = new StringJoiner(", ");
            for (int i = 0; i < bridgeList.size() - 1; i++) {
                joiner.add(bridgeList.get(i));
            }
            return "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: " + joiner.toString() + ", and " + bridgeList.get(bridgeList.size() - 1) + ".";
        }
    }

    public static String generateNewText(String inputText) {
        String[] words = inputText.replaceAll("[^A-Za-z]", " ").toLowerCase().split("\\s+");
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            newText.append(word1);
            String bridges = queryBridgeWords(word1, word2);
            if (bridges.startsWith("The bridge")) {
                // 提取bridge word
                int idx = bridges.indexOf(":");
                if (idx != -1) {
                    String bridgePart = bridges.substring(idx + 1).trim();
                    bridgePart = bridgePart.replaceAll("\\.", ""); // 去掉最后的句号
                    bridgePart = bridgePart.replaceAll(" and ", ", "); // 把and替换成逗号
                    String[] bridgeWords = bridgePart.split(",\\s*"); // 按逗号分隔
                    String bridgeWord = bridgeWords[rand.nextInt(bridgeWords.length)].trim();
                    newText.append(" ").append(bridgeWord);
                }
            }
            newText.append(" ");
        }
        newText.append(words[words.length - 1]);
        return newText.toString();
    }
    public static String calcShortestPath(String word1, String word2) {
        if (!graph.containsKey(word1)) return "起点 " + word1 + " 不存在于图中。";

        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distance::get));

        // 修复：初始化所有节点的距离，包括邻居节点
        Set<String> allNodes = new HashSet<>();
        for (String node : graph.keySet()) {
            allNodes.add(node);
            allNodes.addAll(graph.get(node).keySet());
        }

        for (String word : allNodes) {
            distance.put(word, Integer.MAX_VALUE);
        }
        distance.put(word1, 0);
        pq.add(word1);

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (current.equals(word2)) break;

            Map<String, Integer> neighbors = graph.getOrDefault(current, Collections.emptyMap());
            for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                int alt = distance.get(current) + neighbor.getValue();
                if (alt < distance.getOrDefault(neighbor.getKey(), Integer.MAX_VALUE)) {
                    distance.put(neighbor.getKey(), alt);
                    prev.put(neighbor.getKey(), current);
                    pq.add(neighbor.getKey());
                }
            }
        }

        if (!prev.containsKey(word2) && !word1.equals(word2)) return "No path from " + word1 + " to " + word2;

        List<String> path = new ArrayList<>();
        for (String at = word2; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return "Shortest path: " + String.join(" -> ", path) + " (Weight: " + distance.get(word2) + ")";
    }

    public static double calPageRank(String target) {
        double d = 0.85;
        Set<String> allNodes = new HashSet<>();
        for (String node : graph.keySet()) {
            allNodes.add(node);
            allNodes.addAll(graph.get(node).keySet());
        }
        int N = allNodes.size();

        Map<String, Double> rank = new HashMap<>();
        for (String node : allNodes) rank.put(node, 1.0 / N);

        for (int iter = 0; iter < 1000; iter++) {
            Map<String, Double> newRank = new HashMap<>();
            double danglingSum = 0.0;

            // 先累积悬挂节点的贡献
            for (String node : allNodes) {
                Map<String, Integer> outEdges = graph.getOrDefault(node, Collections.emptyMap());
                if (outEdges.isEmpty()) {
                    danglingSum += rank.get(node);
                }
            }

            // 更新每个节点的新PR值
            for (String node : allNodes) {
                double sum = 0.0;
                for (String other : allNodes) {
                    Map<String, Integer> outEdges = graph.getOrDefault(other, Collections.emptyMap());
                    if (outEdges.containsKey(node)) {
                        int totalWeight = outEdges.values().stream().mapToInt(i -> i).sum();
                        sum += rank.get(other) * outEdges.get(node) / (double) totalWeight;
                    }
                }
                // (1-d)/N 是随机跳转项，d * danglingSum/N 是悬挂节点的贡献
                newRank.put(node, (1 - d) / N + d * (sum + danglingSum / N));
            }

            rank = newRank;
        }
        return rank.getOrDefault(target, 0.0);
    }
    public static String randomWalk() {
        if (graph.isEmpty()) return "图为空";
        StringBuilder walk = new StringBuilder();
        List<String> keys = new ArrayList<>(graph.keySet());
        String current = keys.get(rand.nextInt(keys.size()));
        Set<String> visitedEdges = new HashSet<>();
        walk.append(current);

        while (true) {
            Map<String, Integer> neighbors = graph.get(current);
            if (neighbors == null || neighbors.isEmpty()) break;

            // 构建加权选择池（按边权重决定概率）
            List<String> choices = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    choices.add(entry.getKey());
                }
            }
            if (choices.isEmpty()) break;

            String next = choices.get(rand.nextInt(choices.size()));
            String edgeId = current + "->" + next;

            if (visitedEdges.contains(edgeId)) break;
            visitedEdges.add(edgeId);

            walk.append(" ").append(next);
            current = next;
        }

        String result = walk.toString();
        try {
            FileWriter writer = new FileWriter("random_walk_result.txt");
            writer.write(result);
            writer.close();
        } catch (IOException e) {
            return "写入文件失败：" + e.getMessage();
        }
        return result;
    }
    public static Map<String, Map<String, Integer>> getGraph() {
        return graph;
    }
}
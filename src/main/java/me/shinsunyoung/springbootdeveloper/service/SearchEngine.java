package me.shinsunyoung.springbootdeveloper.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class SearchEngine{

    final double k1 = 1.5;
    final double b = 0.75;
    Map<Long, String> _documents = new HashMap<>();
    Map<String, Map<Long, Integer>> _index = new HashMap<>();
    Double _avdl = null;

    SearchEngine(){
        System.out.println("SearchEngine created");
        System.out.println("k1="+k1+"\nb="+b);
    }

    // null, 부호제거, 소문자변환, 앞뒤공백제거,
    public String normalizeString(String inputString){
        if(inputString == null){
            return "";
        }

        return inputString
                .replaceAll("\\p{Punct}", " ")
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", " ");
    }

    // 공백 기준 토큰 + 한글 2-gram 토큰 생성
    public List<String> tokenize(String inputString){
        String normalized = normalizeString(inputString);
        List<String> words = new ArrayList<>();

        if(normalized.isBlank()){
            return words;
        }

        for(String word : normalized.split(" ")){
            if(!word.isBlank()){
                words.add(word);
                addKoreanBigrams(word, words);
            }
        }

        return words;
    }


    // 한글 적용을 위해 2-gram 방식 토큰화를 추가
    private void addKoreanBigrams(String word, List<String> words){
        StringBuilder hangulPart = new StringBuilder();

        for(int i = 0; i < word.length(); i++){
            char ch = word.charAt(i);

            if(isHangul(ch)){
                hangulPart.append(ch);
            } else {
                addBigrams(hangulPart.toString(), words);
                hangulPart.setLength(0);
            }
        }

        addBigrams(hangulPart.toString(), words);
    }

    private void addBigrams(String text, List<String> words){
        if(text.length() <= 2){
            return;
        }

        for(int i = 0; i < text.length() - 1; i++){
            words.add(text.substring(i, i + 2));
        }
    }

    private boolean isHangul(char ch){
        Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
        return block == Character.UnicodeBlock.HANGUL_SYLLABLES;
    }

    public int numberOfDocuments(){
        return _documents.size();
    }

    // 평균 문서 길이
    public double avdl(){
        if(_documents.isEmpty()){
            return 0.0;
        }

        // index()에서 문서 추가시 길이변경을 위해 _avdl=null로 초기화
        if(_avdl == null){
            int totalLength = 0;
            for(String document : _documents.values()){
                totalLength += tokenize(document).size();
            }
            _avdl = (double) totalLength / _documents.size();
        }

        return _avdl;
    }

    // keyword의 희귀도를 계산
    public double idf(String kw){
        int N = numberOfDocuments();
        int nKw = get_urls(kw).size();	// 키워드가 등장한 문서 수

        if(N == 0){
            return 0.0;
        }

        return Math.log(((N - nKw + 0.5) / (nKw + 0.5)) + 1);
    }

    // 키워드에 대한 문서별 BM25 점수를 계산, 반복성/희귀성/길이를 고려한 점수
    public Map<Long, Double> bm25(String kw){
        Map<Long, Double> result = new HashMap<>();
        Map<Long, Integer> urls = get_urls(kw);

        if(urls.isEmpty()){
            return result;
        }

        double idfScore = idf(kw);
        double averageDocumentLength = avdl();

        if(averageDocumentLength == 0.0){
            return result;
        }

        for(Map.Entry<Long, Integer> entry : urls.entrySet()){
            Long newsId = entry.getKey();
            int freq = entry.getValue();
            int documentLength = tokenize(_documents.get(newsId)).size();

            // bm25 algorithm
            double numerator = freq * (k1 + 1);
            double denominator = freq + k1 * (1 - b + b * documentLength / averageDocumentLength);
            double score = idfScore * numerator / denominator;

            result.put(newsId, score);
        }

        return result;
    }

    // 사용자가 입력한 검색어 처리
    public Map<Long, Double> search(String query){
        Map<Long, Double> newsScores = new HashMap<>();

        for(String keyword : tokenize(query)){
            Map<Long, Double> keywordScores = bm25(keyword);

            for(Map.Entry<Long, Double> entry : keywordScores.entrySet()){
                Long newsId = entry.getKey();
                double score = entry.getValue();
                newsScores.put(newsId, newsScores.getOrDefault(newsId, 0.0) + score);
            }
        }

        return sortByScoreDesc(newsScores);
    }

    // 문서를 엔진에 등록
    public void index(Long newsId, String content){
        _documents.put(newsId, content);

        for(String word : tokenize(content)){
            _index
                    .computeIfAbsent(word, key -> new HashMap<>()) // 단어가 없으면 새 HashMap 생성
                    .put(newsId, _index.get(word).getOrDefault(newsId, 0) + 1);
        }

        _avdl = null;
    }

    // 여러 문서를 엔진에 등록
    public void bulkIndex(Map<Long, String> documents){
        for(Map.Entry<Long, String> entry : documents.entrySet()){
            index(entry.getKey(), entry.getValue());
        }
    }

    // 키워드가 들어간 문서 목록과 횟수를 반환
    public Map<Long, Integer> get_urls(String keyword){
        String normalizedKeyword = normalizeString(keyword);
        return _index.getOrDefault(normalizedKeyword, new HashMap<>());
    }

    // 검색 결과를 내림차순으로 정렬, HashMap은 순서를 보장하지 않음.
    private Map<Long, Double> sortByScoreDesc(Map<Long, Double> scores){
        List<Map.Entry<Long, Double>> entries = new ArrayList<>(scores.entrySet());
        entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<Long, Double> sortedScores = new LinkedHashMap<>();
        for(Map.Entry<Long, Double> entry : entries){
            sortedScores.put(entry.getKey(), entry.getValue());
        }

        return sortedScores;
    }



    // 초기화 메서드 추가
    public void clear(){
        _documents.clear();
        _avdl = null;
        _index.clear();
    }

    // 인덱스 출력용 메서드
    public void printEngineIndex(String keyword){
        for(String token: tokenize(keyword)){
            Map<Long, Integer> counts = get_urls(token);
            System.out.println("search:"+token);
            if(counts.isEmpty()){
                System.out.println("no index");
                continue;
            }
            for (Map.Entry<Long, Integer> entry : counts.entrySet()) {
                System.out.println("\tnewsId:"+entry.getKey() + ", count:" + entry.getValue());
            }
        }
    }

}


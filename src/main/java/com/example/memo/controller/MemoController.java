package com.example.memo.controller;

import com.example.memo.dto.MemoRequestDto;
import com.example.memo.dto.MemoResponseDto;
import com.example.memo.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
// -> JSON 으로 사용하려고
@RequestMapping("/memos")
// -> prefix 하는 URL을 설정할 때 사용
public class MemoController {

    private final Map<Long, Memo> memoList = new HashMap<>();
    // -> Map 도 인터페이스이기 때문에 구현체인 HashMap<>()으로 초기화

    @PostMapping // -> 생성이기 때문에
    public ResponseEntity<MemoResponseDto> createMemo(@RequestBody MemoRequestDto dto) {
        // 식별자가 1씩 증가 하도록 만들기
        Long memoId = memoList.isEmpty() ? 1 : Collections.max(memoList.keySet()) + 1;

        // 요청받은 데이터로 Memo 객체 생성
        Memo memo = new Memo(memoId, dto.getTitle(), dto.getContents());

        // Inmemory DB에 Memo 메모
        memoList.put(memoId, memo);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.CREATED);
    }

    @GetMapping
    // -> 뒤에 괄호없이 아무것도 적지 않으면
    // -> 맨위에 @RequestMapping 의 URL이 Mapping 됨.
    public List<MemoResponseDto> findAllMemos() {
        // init List
        List<MemoResponseDto> responseList = new ArrayList<>();
        // -> 인터페이스는 new에서 인스턴스화 할 수 없다. :: 그래서 항상 구현체 사용!!

        // HashMap<Memo> -> List<MemoResponseDto>
        for (Memo memo : memoList.values()) {
            MemoResponseDto responseDto = new MemoResponseDto(memo);
            responseList.add(responseDto);
            // -> 반복문이 반복되면서 memo 객체가 responseDto로 변하고
            // -> 변한 responseDto가 add 됨.
        }

        // Map To List
//        responseList = memoList.values().stream().map(MemoResponseDto::new).toList();
        // -> 위의 반복문 for 문과 결과는 동일.
        // :: stream 사용이 익숙해지면 for 문 대신 사용 하기!

        return responseList;
    }

    @GetMapping("/{id}")
    // -> 조회이기 때문에
    public ResponseEntity<MemoResponseDto> findMemoById(@PathVariable Long id) {
        Memo memo = memoList.get(id);

        if(memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    // -> 전체 수정
    public ResponseEntity<MemoResponseDto> updateMemoById(@PathVariable Long id, @RequestBody MemoRequestDto dto) {
        Memo memo = memoList.get(id);

        if(memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (dto.getTitle() == null || dto.getContents() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        memo.update(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateTitle(@PathVariable Long id, @RequestBody MemoRequestDto dto) {
        Memo memo = memoList.get(id);

        // NPE 방지
        if(memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (dto.getTitle() == null || dto.getContents() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        memo.updateTitle(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    // -> 삭제여서 return 하는 데이터가 없기 때문에 void
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {

        // memoList의 Key값에 id를 포함하고 있다면
        if (memoList.containsKey(id)) {
            memoList.remove(id);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}

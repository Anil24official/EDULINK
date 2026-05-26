package com.edulink.complianceservice.controller;

import com.edulink.complianceservice.dto.RegulatorDto;
import com.edulink.complianceservice.dto.ReportDto;
import com.edulink.complianceservice.dto.RuleDto;

import com.edulink.complianceservice.entity.Regulator;
import com.edulink.complianceservice.entity.Rule;
import com.edulink.complianceservice.service.BoardService;
import com.edulink.complianceservice.service.ComplianceService;
import com.edulink.complianceservice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private BoardService boardService;

    @Autowired
    private ComplianceService complianceService;

//    @GetMapping("/schools/{schoolId}")
//    public ResponseEntity<SchoolDto> getSchoolById(@PathVariable String schoolId){
//            SchoolDto schools= boardService.getSchoolById(schoolId);
//            return ResponseEntity.ok(schools);
//    }

//    @PostMapping("/audit-performance-create")
//    public ResponseEntity<Performance> setPerformance(@RequestBody PerformanceDto performanceDto){
//          return ResponseEntity.ok(boardService.saveData(performanceDto));
//    }

//    @GetMapping("/audit-performance")
//    public ResponseEntity<List<Performance>> getPerformance(){
//        return ResponseEntity.ok(boardService.getAllData());
//    }

//    @GetMapping("/report/{schoolId}")
//    public ResponseEntity<Map<String,String>> report(@PathVariable String schoolId){
//        return ResponseEntity.ok(boardService.getAllReport(schoolId));
//    }


//    ------------------------------Somthing new---------------------------------


    @GetMapping("/allRules")
    public ResponseEntity<List<RuleDto>> allRules(){
        return ResponseEntity.ok(RuleDto.fromEntities(boardService.getRules()));
    }

    @GetMapping("/usersStatus")
    public ResponseEntity<Map<String,Integer>> getUsers(){
        return ResponseEntity.ok(boardService.getUsers());
    }

    @GetMapping("/reports")
    public ResponseEntity<ReportDto> getReports(){
        return ResponseEntity.ok(boardService.getReport());
    }

    @PostMapping("/rule-create")
    @PreAuthorize("hasRole('EDUCATION_BOARD_OFFICER')")
    public ResponseEntity<RuleDto> ruleCreate(@RequestBody RuleDto ruleDto){
        Rule saved = boardService.rulesCreate(ruleDto);
        return ResponseEntity.ok(RuleDto.fromEntity(saved));
    }

    @PostMapping("/rule-review")
    @PreAuthorize("hasRole('EDUCATION_BOARD_OFFICER')")
    public ResponseEntity<RuleDto> ruleReview(@RequestBody RuleDto ruleDto){
        Rule saved = boardService.ruleReview(ruleDto);
        return ResponseEntity.ok(RuleDto.fromEntity(saved));
    }

    @DeleteMapping("/rule-delete/{id}")
    @PreAuthorize("hasRole('EDUCATION_BOARD_OFFICER')")
    public ResponseEntity<String> ruleDelete(@PathVariable(name="id") Long id){
        System.out.println("id = "+id);
        return ResponseEntity.ok(boardService.ruleDelete(id));
    }

    @GetMapping("/getRegulatorReviewById/{id}")
    public ResponseEntity<RegulatorDto> getRegulatorReviewById(@PathVariable("id") Long id){
        Regulator regulator = boardService.getRegulatorReviewById(id);
        return ResponseEntity.ok(RegulatorDto.fromEntity(regulator));
    }

    @PutMapping("/ruleActivate/{ruleId}/{active}")
    @PreAuthorize("hasRole('EDUCATION_BOARD_OFFICER')")
    public ResponseEntity<RuleDto> ruleActivate(@PathVariable Long ruleId,@PathVariable boolean active){
        Rule saved = boardService.activeRule(ruleId, active);
        return ResponseEntity.ok(RuleDto.fromEntity(saved));
    }

}

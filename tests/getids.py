import ijson

# Path to the large JSON file
file_path = '/mnt/f/Users/Obioma/Downloads/qv-toa-2023-1M-anant/qv-toa-2023-1M.json'
milestones = [100, 500, 1000, 4000, 5000, 5001]

results = []
# Open the file for reading
with open(file_path, 'r') as f:
    items = ijson.items(f, '', multiple_values=True)
    i = 0
    while i < milestones[-1]:
        try: 
            item = next(items) 
            # Do something with each item
            results.append(item["id"])
            #print(item["id"])  # or process it in memory
            for j in range(len(milestones)):
                if i == milestones[j]:
                    milestone_file = f'milestone_{milestones[j]}.txt'
                    outfile = open(milestone_file, mode='w', encoding='utf-8')
                    outfile.write(str(results).replace("'",'"'))
                    outfile.close()
            i += 1
        except StopIteration:
            break
#    print(results)
